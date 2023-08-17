package se.ansman.dagger.auto.compiler.common

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName


@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("This type is already a class name", level = DeprecationLevel.ERROR)
fun ClassName.rawType(): ClassName = this

fun TypeName.rawType(): ClassName =
    when (this) {
        is ClassName -> this
        is ParameterizedTypeName -> rawType
        else -> throw IllegalArgumentException("Cannot get class name from $this")
    }