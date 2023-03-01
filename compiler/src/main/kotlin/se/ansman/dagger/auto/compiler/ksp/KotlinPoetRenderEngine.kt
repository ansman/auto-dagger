package se.ansman.dagger.auto.compiler.ksp

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.Dynamic
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import se.ansman.dagger.auto.compiler.processing.RenderEngine
import kotlin.reflect.KClass

object KotlinPoetRenderEngine : RenderEngine<TypeName, ClassName, AnnotationSpec> {
    override fun className(packageName: String, simpleName: String): ClassName = ClassName(packageName, simpleName)
    override fun className(qualifiedName: String): ClassName = ClassName.bestGuess(qualifiedName)
    override fun className(type: KClass<*>): ClassName = type.asClassName()

    override fun topLevelClassName(className: ClassName): ClassName = className.topLevelClassName()
    override fun packageName(className: ClassName): String = className.packageName
    override fun simpleNames(className: ClassName): List<String> = className.simpleNames
    override fun simpleName(className: ClassName): String = className.simpleName

    override fun rawType(typeName: TypeName): ClassName =
        when (typeName) {
            is ClassName -> typeName
            is ParameterizedTypeName -> typeName.rawType
            Dynamic,
            is LambdaTypeName,
            is TypeVariableName,
            is WildcardTypeName,
            -> throw IllegalArgumentException("Type ${typeName.javaClass} isn't supported")
        }
}