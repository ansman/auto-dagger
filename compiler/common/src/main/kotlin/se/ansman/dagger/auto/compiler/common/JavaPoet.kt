package se.ansman.dagger.auto.compiler.common

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("This type is already a class name", level = DeprecationLevel.ERROR)
fun ClassName.rawType(): ClassName = this

fun TypeName.rawType(): ClassName =
    when (val boxed = box()) {
        is ClassName -> boxed
        is ParameterizedTypeName -> boxed.rawType
        else -> error("Cannot get raw type for $this (of type ${this.javaClass})")
    }