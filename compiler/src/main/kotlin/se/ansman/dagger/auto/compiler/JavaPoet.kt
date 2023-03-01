package se.ansman.dagger.auto.compiler

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("This type is already a class name", level = DeprecationLevel.ERROR)
fun ClassName.asClassName(): ClassName = this

fun TypeName.asClassName(): ClassName =
    when (this) {
        is ClassName -> this
        is ParameterizedTypeName -> rawType
        else -> throw IllegalArgumentException("Cannot get class name from $this")
    }