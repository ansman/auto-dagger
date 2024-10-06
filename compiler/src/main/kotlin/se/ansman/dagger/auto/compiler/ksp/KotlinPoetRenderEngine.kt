package se.ansman.dagger.auto.compiler.ksp

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.Dynamic
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.processing.model.AnnotationNode
import kotlin.reflect.KClass

object KotlinPoetRenderEngine : RenderEngine<KSNode, TypeName, ClassName, AnnotationSpec> {
    override fun className(packageName: String, simpleName: String): ClassName = ClassName(packageName, simpleName)
    override fun className(qualifiedName: String): ClassName = ClassName.bestGuess(qualifiedName)
    override fun className(type: KClass<*>): ClassName = type.asClassName()

    override fun qualifiedName(className: ClassName): String = className.canonicalName

    override fun simpleName(typeName: TypeName): String =
        when (typeName) {
            is ClassName -> typeName.simpleName
            Dynamic -> "Dynamic"
            is LambdaTypeName -> "Function"
            is ParameterizedTypeName -> typeName.typeArguments
                .joinToString(prefix = "${typeName.rawType.simpleName}Of", separator = "And") { simpleName(it) }

            is TypeVariableName -> typeName.name
            is WildcardTypeName -> when {
                typeName == STAR -> "Star"
                typeName.inTypes.isNotEmpty() -> typeName.inTypes
                    .joinToString(prefix = "WildcardOf", separator = "And") { simpleName(it) }
                else -> typeName.outTypes
                    .joinToString(prefix = "WildcardOf", separator = "And") { simpleName(it) }
            }
        }
    override fun simpleNames(className: ClassName): List<String> = className.simpleNames
    override fun topLevelClassName(className: ClassName): ClassName = className.topLevelClassName()
    override fun packageName(className: ClassName): String = className.packageName

    override fun rawType(typeName: TypeName): ClassName =
        when (typeName) {
            is ClassName -> typeName
            is ParameterizedTypeName -> typeName.rawType
            Dynamic,
            is LambdaTypeName,
            is TypeVariableName,
            is WildcardTypeName -> throw IllegalArgumentException("Type ${typeName.javaClass} isn't supported")
        }

    override fun asWildcard(typeName: TypeName): TypeName =
        when (typeName) {
            is ClassName,
            Dynamic,
            is LambdaTypeName -> typeName

            is ParameterizedTypeName -> typeName.copy(typeArguments = typeName.typeArguments.map { STAR })
            is TypeVariableName,
            is WildcardTypeName -> STAR
        }

    override fun AnnotationNode<KSNode, TypeName, ClassName>.toAnnotationSpec(): AnnotationSpec =
        (node as KSAnnotation).toAnnotationSpecFixed()
}