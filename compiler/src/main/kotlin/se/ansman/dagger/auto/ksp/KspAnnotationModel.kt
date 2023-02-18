package se.ansman.dagger.auto.ksp

import com.google.devtools.ksp.isDefault
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import se.ansman.dagger.auto.applyEach
import se.ansman.dagger.auto.models.AnnotationModel
import java.util.Locale
import kotlin.reflect.KClass

class KspAnnotationModel(private val annotation: KSAnnotation) : AnnotationModel<AnnotationSpec> {
    override val declaredAnnotations: List<KspAnnotationModel> by lazy {
        annotation.annotationType.resolve().declaration.annotations.map(::KspAnnotationModel).toList()
    }

    override fun isOfType(type: KClass<*>): Boolean =
        annotation.shortName.asString() == type.simpleName &&
                type.asClassName() == annotation.annotationType.resolve().toClassName()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getValue(name: String): T? =
        annotation.arguments.find { it.name?.asString() == name }
            ?.takeUnless { it.isDefault() }
            ?.value as T?

    override fun toAnnotationSpec(): AnnotationSpec = annotation.toAnnotationSpec()
}

private fun KSAnnotation.toAnnotationSpec(): AnnotationSpec =
    AnnotationSpec
        .builder(annotationType.resolve().unwrapTypeAlias().toClassName())
        .applyEach(arguments) { argument ->
            if (argument.isDefault()) {
                return@applyEach
            }
            addMember(CodeBlock.builder()
                .add("%N = ", argument.name!!.getShortName())
                .addAnnotationValue(argument.value!!)
                .build())
        }
        .build()

private fun KSType.unwrapTypeAlias(): KSType = (declaration as? KSTypeAlias)?.type?.resolve() ?: this

private fun CodeBlock.Builder.addAnnotationValue(value: Any): CodeBlock.Builder =
    when (value) {
        is List<*> -> {
            add("[")
            value.forEachIndexed { index, innerValue ->
                if (index > 0) add(", ")
                addAnnotationValue(innerValue!!)
            }
            add("]")
        }
        is KSType -> {
            val unwrapped = value.unwrapTypeAlias()
            if ((unwrapped.declaration as KSClassDeclaration).classKind == ClassKind.ENUM_ENTRY) {
                val parent = unwrapped.declaration.parentDeclaration as KSClassDeclaration
                val entry = unwrapped.declaration.simpleName.getShortName()
                add("%T.%L", parent.toClassName(), entry)
            } else {
                add("%T::class", unwrapped.toClassName())
            }
        }
        is KSName -> add("%T.%L", ClassName.bestGuess(value.getQualifier()), value.getShortName())
        is KSAnnotation -> add("%L", value.toAnnotationSpec())
        else -> add(memberForValue(value))
    }

private fun memberForValue(value: Any) = when (value) {
    is Class<*> -> CodeBlock.of("%T::class", value)
    is Enum<*> -> CodeBlock.of("%T.%L", value.javaClass, value.name)
    is String -> CodeBlock.of("%S", value)
    is Char -> CodeBlock.of("'%L'", value.literalWithoutSingleQuotes())
    is Float -> CodeBlock.of("%Lf", value)
    else -> CodeBlock.of("%L", value)
}

private fun Char.literalWithoutSingleQuotes(): String =
    when (this) {
        '\b' -> "\\b" /* \u0008: backspace (BS) */
        '\t' -> "\\t" /* \u0009: horizontal tab (HT) */
        '\n' -> "\\n" /* \u000a: linefeed (LF) */
        '\u000c' -> "\\u000c" /* \u000c: form feed (FF) */
        '\r' -> "\\r" /* \u000d: carriage return (CR) */
        '\'' -> "\\'" /* \u0027: single quote (') */
        '\\' -> "\\\\" /* \u005c: backslash (\) */
        else -> if (isISOControl()) String.format(Locale.ROOT, "\\u%04x", code) else toString()
    }
