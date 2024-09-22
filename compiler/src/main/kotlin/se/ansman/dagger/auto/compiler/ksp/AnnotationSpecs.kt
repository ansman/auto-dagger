package se.ansman.dagger.auto.compiler.ksp

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock

fun <T> AnnotationSpec.Builder.addMemberArray(name: String, values: Iterable<T>, render: (T) -> CodeBlock) = apply {
    val renderedValues = values.map(render)
    addMember(
        renderedValues.joinToString(prefix = "%L = [", postfix = "]") { "%L" },
        name,
        *renderedValues.toTypedArray()
    )
}

fun AnnotationSpec.Builder.addMemberClassArray(name: String, values: Iterable<ClassName>) =
    addMemberArray(name, values) { CodeBlock.of("%T::class", it) }