package se.ansman.deager.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName

fun KSType.toTypeName(resolver: Resolver): TypeName {
    val className = declaration.toClassName(resolver)
    return if (arguments.isEmpty()) {
        className
    } else {
        ParameterizedTypeName.get(className, *arguments.map { it.type!!.resolve().toTypeName(resolver) }.toTypedArray())
    }
}

@OptIn(KspExperimental::class)
fun KSDeclaration.toClassName(resolver: Resolver): ClassName =
    parentDeclaration
        ?.toClassName(resolver)
        ?.nestedClass(simpleName.asString())
        ?: qualifiedName?.let(resolver::mapKotlinNameToJava)?.let { ClassName.bestGuess(it.asString()) }
        ?: ClassName.get(packageName.asString(), simpleName.asString())