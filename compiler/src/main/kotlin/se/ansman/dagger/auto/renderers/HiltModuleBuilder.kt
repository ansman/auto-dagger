package se.ansman.dagger.auto.renderers

import dagger.Lazy
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import javax.inject.Provider
import kotlin.reflect.KClass

interface HiltModuleBuilder<Node, TypeName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile> {
    fun addProvider(
        name: String,
        parameters: List<Parameter<TypeName, AnnotationSpec>> = emptyList(),
        mode: ProviderMode<AnnotationSpec> = ProviderMode.Single,
        returnType: DaggerType<TypeName, AnnotationSpec>,
        isPublic: Boolean,
        originatingElement: Node,
        contents: (List<ParameterSpec>) -> CodeBlock,
    )

    fun addBinding(
        name: String,
        sourceType: DaggerType<TypeName, AnnotationSpec>,
        mode: ProviderMode<AnnotationSpec> = ProviderMode.Single,
        returnType: DaggerType<TypeName, AnnotationSpec>,
        isPublic: Boolean,
        originatingElement: Node,
    )

    fun build(): SourceFile

    sealed class Parameter<out TypeName, AnnotationSpec> {
        abstract val qualifiers: Set<AnnotationSpec>
    }

    data class Lazy<out TypeName, AnnotationSpec>(
        val type: Parameter<TypeName, AnnotationSpec>,
    ) : Parameter<TypeName, AnnotationSpec>() {
        override val qualifiers: Set<AnnotationSpec> get() = type.qualifiers

        constructor(
            type: TypeName,
            qualifiers: Set<AnnotationSpec> = emptySet(),
        ) : this(DaggerType(type, qualifiers))
    }

    data class Provider<out TypeName, AnnotationSpec>(
        val type: Parameter<TypeName, AnnotationSpec>,
    ) : Parameter<TypeName, AnnotationSpec>() {
        override val qualifiers: Set<AnnotationSpec> get() = type.qualifiers

        constructor(
            type: TypeName,
            qualifiers: Set<AnnotationSpec> = emptySet(),
        ) : this(DaggerType(type, qualifiers))
    }

    data class DaggerType<out TypeName, AnnotationSpec>(
        val type: TypeName,
        override val qualifiers: Set<AnnotationSpec> = emptySet(),
    ) : Parameter<TypeName, AnnotationSpec>()

    sealed class ProviderMode<out AnnotationSpec> {
        object Single : ProviderMode<Nothing>()
        object IntoSet : ProviderMode<Nothing>()
        data class IntoMap<AnnotationSpec>(val bindingKey: AnnotationSpec) : ProviderMode<AnnotationSpec>()
    }
}

fun <AnnotationSpec> HiltModuleBuilder.ProviderMode<AnnotationSpec>.asAnnotations(
    annotationSpecFromAnnotation: (Annotation) -> AnnotationSpec,
) =
    when (this) {
        HiltModuleBuilder.ProviderMode.Single -> listOf()
        HiltModuleBuilder.ProviderMode.IntoSet -> listOf(annotationSpecFromAnnotation(IntoSet()))
        is HiltModuleBuilder.ProviderMode.IntoMap -> listOf(annotationSpecFromAnnotation(IntoMap()), bindingKey)
    }

fun <TypeName> HiltModuleBuilder.Parameter<TypeName, *>.asParameterName(simpleName: TypeName.() -> String): String =
    when (this) {
        is HiltModuleBuilder.Lazy -> "lazy${type.asParameterName(simpleName).replaceFirstChar(Char::uppercaseChar)}"
        is HiltModuleBuilder.Provider -> "${
            type.asParameterName(simpleName).replaceFirstChar(Char::lowercaseChar)
        }Provider"

        is HiltModuleBuilder.DaggerType -> type.simpleName().replaceFirstChar(Char::lowercaseChar)
    }

fun <TypeName> HiltModuleBuilder.Parameter<TypeName, *>.asTypeName(
    parameterizedTypeName: (rawType: KClass<*>, arguments: List<TypeName>) -> TypeName,
): TypeName = when (this) {
    is HiltModuleBuilder.Lazy -> parameterizedTypeName(lazy, listOf(type.asTypeName(parameterizedTypeName)))
    is HiltModuleBuilder.Provider -> parameterizedTypeName(provider, listOf(type.asTypeName(parameterizedTypeName)))
    is HiltModuleBuilder.DaggerType -> type
}

private val lazy = Lazy::class
private val provider = Provider::class