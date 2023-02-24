package se.ansman.dagger.auto

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName


@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("This type is already a class name", level = DeprecationLevel.ERROR)
fun ClassName.asClassName(): ClassName = this

fun TypeName.asClassName(): ClassName =
    when (this) {
        is ClassName -> this
        is ParameterizedTypeName -> rawType
        else -> throw IllegalArgumentException("Cannot get class name from $this")
    }