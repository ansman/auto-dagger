package se.ansman.dagger.auto.compiler.ksp

import com.google.devtools.ksp.isDefault
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ksp.toClassName
import se.ansman.dagger.auto.compiler.common.applyEachIndexed
import se.ansman.dagger.auto.compiler.ksp.processing.unwrapTypeAlias
import java.util.Locale

/**
 * A version of `toAnnotationSpec` that fixes char arguments.
 *
 * Can be removed in favor for `toAnnotationSpec` after 1.12
 */
fun KSAnnotation.toAnnotationSpecFixed(): AnnotationSpec =
    AnnotationSpec
        .builder(annotationType.resolve().unwrapTypeAlias().toClassName())
        .applyEachIndexed(arguments) { i, argument ->
            if (argument.isDefault()) {
                return@applyEachIndexed
            }
            addMember(
                CodeBlock.builder()
                    .apply {
                        val name = argument.name!!.getShortName()
                        if (i > 0 || name != "value") {
                            add("%N = ", name)
                        }
                    }
                    .addAnnotationValue(argument.value!!)
                    .build()
            )
        }
        .build()

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
        is KSAnnotation -> add("%L", value.toAnnotationSpecFixed())
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
