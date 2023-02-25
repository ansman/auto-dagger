package se.ansman.dagger.auto.kapt

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.processing.RenderEngine
import kotlin.reflect.KClass

object JavaPoetRenderEngine : RenderEngine<TypeName, ClassName, AnnotationSpec> {
    override fun className(packageName: String, simpleName: String): ClassName = ClassName.get(packageName, simpleName)
    override fun className(qualifiedName: String): ClassName = ClassName.bestGuess(qualifiedName)
    override fun className(type: KClass<*>): ClassName = ClassName.get(type.java)

    override fun simpleName(className: ClassName): String = className.simpleName()
    override fun simpleNames(className: ClassName): List<String> = className.simpleNames()
    override fun packageName(className: ClassName): String = className.packageName()
    override fun topLevelClassName(className: ClassName): ClassName = className.topLevelClassName()

    override fun rawType(typeName: TypeName): ClassName =
        when (typeName) {
            is ClassName -> typeName
            is ParameterizedTypeName -> typeName.rawType
            else -> error("Cannot get raw type for $typeName (of type ${typeName.javaClass})")
        }
}