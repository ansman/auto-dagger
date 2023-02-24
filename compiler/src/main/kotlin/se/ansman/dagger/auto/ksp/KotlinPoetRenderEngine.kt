package se.ansman.dagger.auto.ksp

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.Dynamic
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import se.ansman.dagger.auto.processing.RenderEngine
import kotlin.reflect.KClass

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
object KotlinPoetRenderEngine : RenderEngine<TypeName, ClassName, AnnotationSpec> {
    override fun className(packageName: String, simpleName: String): ClassName = ClassName(packageName, simpleName)

    override fun className(qualifiedName: String): ClassName = ClassName.bestGuess(qualifiedName)

    override fun KClass<*>.toClassName(): ClassName = asClassName()

    override val TypeName.rawType: ClassName
        get() = when (this) {
            is ClassName -> this
            is ParameterizedTypeName -> rawType
            Dynamic,
            is LambdaTypeName,
            is TypeVariableName,
            is WildcardTypeName -> throw IllegalArgumentException("Type $javaClass isn't supported")
        }

    override fun ClassName.peerClass(name: String): ClassName = peerClass(name)

    override val ClassName.topLevelClassName: ClassName
        get() = topLevelClassName()

    override val ClassName.packageName: String
        get() = packageName

    override val ClassName.simpleNames: List<String>
        get() = simpleNames

    override val ClassName.simpleName: String
        get() = simpleName

}