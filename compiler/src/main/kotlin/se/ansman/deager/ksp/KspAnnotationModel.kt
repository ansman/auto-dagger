package se.ansman.deager.ksp

import com.google.devtools.ksp.isDefault
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import se.ansman.deager.applyEach
import se.ansman.deager.models.AnnotationModel

class KspAnnotationModel(private val annotation: KSAnnotation, private val resolver: Resolver) : AnnotationModel {
    override val declaredAnnotations: List<KspAnnotationModel> by lazy {
        annotation.annotationType.resolve().declaration.annotations.map { KspAnnotationModel(it, resolver) }.toList()
    }

    override fun isOfType(className: ClassName): Boolean =
        annotation.shortName.asString() == className.simpleName() &&
                className == annotation.annotationType.resolve().declaration.toClassName(resolver)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getValue(name: String): T? =
        annotation.arguments.find { it.name?.asString() == name }
            ?.takeUnless { it.isDefault() }
            ?.value as T?

    override fun toAnnotationSpec(): AnnotationSpec = annotation.toAnnotationSpec(resolver)
}

private fun KSAnnotation.toAnnotationSpec(resolver: Resolver): AnnotationSpec =
    AnnotationSpec
        .builder(annotationType.resolve().unwrapTypeAlias().declaration.toClassName(resolver))
        .applyEach(arguments) { argument ->
            addMember(argument.name!!.getShortName(), CodeBlock.builder().addAnnotationValue(argument.value!!, resolver).build())
        }
        .build()

private fun KSType.unwrapTypeAlias(): KSType = (declaration as? KSTypeAlias)?.type?.resolve() ?: this

private fun CodeBlock.Builder.addAnnotationValue(value: Any, resolver: Resolver): CodeBlock.Builder =
    when (value) {
        is List<*> -> {
            add("{")
            value.forEachIndexed { index, innerValue ->
                if (index > 0) add(", ")
                addAnnotationValue(innerValue!!, resolver)
            }
            add("}")
        }
        is KSType -> {
            val unwrapped = value.unwrapTypeAlias()
            if ((unwrapped.declaration as KSClassDeclaration).classKind == ClassKind.ENUM_ENTRY) {
                val parent = unwrapped.declaration.parentDeclaration as KSClassDeclaration
                val entry = unwrapped.declaration.simpleName.getShortName()
                add("\$T.\$L", parent.toClassName(resolver), entry)
            } else {
                add("\$T.class", unwrapped.declaration.toClassName(resolver))
            }
        }
        is KSName ->
            add(
                "\$T.\$L",
                ClassName.bestGuess(value.getQualifier()),
                value.getShortName(),
            )
        is KSAnnotation -> add("\$L", value.toAnnotationSpec(resolver))
        else -> add(memberForValue(value))
    }

private fun memberForValue(value: Any) = when (value) {
    is Class<*> -> CodeBlock.of("\$T.class", value)
    is Enum<*> -> CodeBlock.of("\$T.\$L", value.javaClass, value.name)
    is String -> CodeBlock.of("\$S", value)
    is Char -> CodeBlock.of("'\$L'", value.literalWithoutSingleQuotes())
    is Float -> CodeBlock.of("\$Lf", value)
    else -> CodeBlock.of("\$L", value)
}

// see https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6
private fun Char.literalWithoutSingleQuotes(): String =
    when (this) {
        '\b' -> "\\b" /* \u0008: backspace (BS) */
        '\t' -> "\\t" /* \u0009: horizontal tab (HT) */
        '\n' -> "\\n" /* \u000a: linefeed (LF) */
        '\u000c' -> "\\u000c" /* \u000c: form feed (FF) */
        '\r' -> "\\r" /* \u000d: carriage return (CR) */
        '\"' -> "\"" /* \u0022: double quote (") */
        '\'' -> "\\'" /* \u0027: single quote (') */
        '\\' -> "\\\\" /* \u005c: backslash (\) */
        else -> if (Character.isISOControl(this)) String.format("\\u%04x", code) else toString()
    }
