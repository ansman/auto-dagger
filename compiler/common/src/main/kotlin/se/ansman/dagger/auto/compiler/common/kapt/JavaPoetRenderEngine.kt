package se.ansman.dagger.auto.compiler.common.kapt

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeVariableName
import com.squareup.javapoet.WildcardTypeName
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.rawType
import kotlin.reflect.KClass

object JavaPoetRenderEngine : RenderEngine<TypeName, ClassName, AnnotationSpec> {
    override fun className(packageName: String, simpleName: String): ClassName = ClassName.get(packageName, simpleName)
    override fun className(qualifiedName: String): ClassName = ClassName.bestGuess(qualifiedName)
    override fun className(type: KClass<*>): ClassName = ClassName.get(type.java)

    override fun typeArguments(typeName: TypeName): List<TypeName> =
        when (typeName) {
            is ParameterizedTypeName -> typeName.typeArguments
            else -> emptyList()
        }

    override fun simpleName(typeName: TypeName): String =
        when (typeName) {
            is ClassName -> typeName.simpleName()
            is ArrayTypeName -> simpleName(typeName.componentType) + "Array"
            is ParameterizedTypeName -> typeName.typeArguments
                .joinToString(prefix = "${typeName.rawType.simpleName()}Of", separator = "And") { simpleName(it) }

            is TypeVariableName -> typeName.name
            is WildcardTypeName -> buildString {
                append("Wildcard")
                if (typeName.lowerBounds.isNotEmpty()) {
                    typeName.lowerBounds.joinTo(this, prefix = "Of", separator = "And") { simpleName(it) }
                } else if (typeName.upperBounds.singleOrNull() != TypeName.OBJECT) {
                    typeName.upperBounds.joinTo(this, prefix = "Of", separator = "And") { simpleName(it) }
                }
            }

            // Primitive
            else -> if (typeName.javaClass == TypeName::class.java) {
                typeName.toString().replaceFirstChar(Char::uppercaseChar)
            } else {
                error("Type ${typeName.javaClass} isn't supported")
            }
        }

    override fun simpleNames(className: ClassName): List<String> = className.simpleNames()
    override fun packageName(className: ClassName): String = className.packageName()
    override fun topLevelClassName(className: ClassName): ClassName = className.topLevelClassName()

    override fun rawType(typeName: TypeName): ClassName = typeName.rawType()

    override fun asWildcard(typeName: TypeName): TypeName =
        when (typeName) {
            is ParameterizedTypeName -> ParameterizedTypeName.get(
                typeName.rawType,
                *typeName.typeArguments.map { WildcardTypeName.subtypeOf(Object::class.java) }.toTypedArray()
            )

            else -> typeName
        }
}