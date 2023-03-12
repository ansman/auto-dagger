package se.ansman.dagger.auto.compiler.kapt

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.compiler.rawType
import se.ansman.dagger.auto.compiler.processing.RenderEngine
import kotlin.reflect.KClass

object JavaPoetRenderEngine : RenderEngine<TypeName, ClassName, AnnotationSpec> {
    override fun className(packageName: String, simpleName: String): ClassName = ClassName.get(packageName, simpleName)
    override fun className(qualifiedName: String): ClassName = ClassName.bestGuess(qualifiedName)
    override fun className(type: KClass<*>): ClassName = ClassName.get(type.java)

    override fun simpleName(className: ClassName): String = className.simpleName()
    override fun simpleNames(className: ClassName): List<String> = className.simpleNames()
    override fun packageName(className: ClassName): String = className.packageName()
    override fun topLevelClassName(className: ClassName): ClassName = className.topLevelClassName()

    override fun rawType(typeName: TypeName): ClassName = typeName.rawType()
}