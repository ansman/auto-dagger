package se.ansman.deager

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName

fun TypeName.asClassName(): ClassName =
    when (this) {
        is ClassName -> this
        is ParameterizedTypeName -> rawType
        else -> throw IllegalArgumentException("Cannot get class name from $this")
    }