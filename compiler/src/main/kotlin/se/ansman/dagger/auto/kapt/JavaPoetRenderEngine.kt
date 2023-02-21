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

    override val ClassName.simpleName: String get() = simpleName()
    override val ClassName.simpleNames: List<String> get() = simpleNames()
    override val ClassName.packageName: String get() = packageName()
    override val ClassName.topLevelClassName: ClassName get() = topLevelClassName()

    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    override fun ClassName.peerClass(name: String): ClassName = peerClass(name)

    override val TypeName.rawType: ClassName
        get() = when (this) {
            is ClassName -> this
            is ParameterizedTypeName -> rawType
            else -> error("Cannot get raw type for $this (of type $javaClass)")
        }

    override fun KClass<*>.toClassName(): ClassName = ClassName.get(java)
}