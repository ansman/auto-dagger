package se.ansman.dagger.auto.compiler.common.rendering

import dagger.Lazy
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import se.ansman.dagger.auto.compiler.common.models.HiltModule
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.DaggerType
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.Parameter
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.ProviderMode
import javax.inject.Provider
import kotlin.reflect.KClass

interface HiltModuleBuilder<Node, TypeName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, Output> {

    fun addProvider(
        name: String,
        returnType: DaggerType<TypeName, AnnotationSpec>,
        isPublic: Boolean,
        parameters: List<Parameter<TypeName, AnnotationSpec>>,
        mode: ProviderMode<AnnotationSpec> = ProviderMode.Single,
        contents: ProviderContext.(List<ParameterSpec>) -> CodeBlock,
    ): HiltModuleBuilder<Node, TypeName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, Output>

    fun addBinding(
        name: String,
        sourceType: DaggerType<TypeName, AnnotationSpec>,
        returnType: DaggerType<TypeName, AnnotationSpec>,
        isPublic: Boolean,
        mode: ProviderMode<AnnotationSpec> = ProviderMode.Single,
    ): HiltModuleBuilder<Node, TypeName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, Output>

    fun addOptionalBinding(
        name: String,
        type: DaggerType<TypeName, AnnotationSpec>,
        isPublic: Boolean,
    ): HiltModuleBuilder<Node, TypeName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, Output>

    fun build(): Output

    fun interface Factory<Node, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile> {
        fun create(
            info: HiltModule<Node, ClassName>,
        ): HiltModuleBuilder<Node, TypeName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>
    }

    sealed class Installation<ClassName> {
        abstract val components: Set<ClassName>

        data class InstallIn<ClassName>(override val components: Set<ClassName>) : Installation<ClassName>() {
            constructor(vararg components: ClassName) : this(components.toSet())
        }

        data class TestInstallIn<ClassName>(
            override val components: Set<ClassName>,
            val replaces: Set<ClassName>,
        ) : Installation<ClassName>()
    }

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
        data object Single : ProviderMode<Nothing>()
        data object IntoSet : ProviderMode<Nothing>()
        data class IntoMap<AnnotationSpec>(val bindingKey: AnnotationSpec) : ProviderMode<AnnotationSpec>()
    }
}

fun <Node, TypeName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile> HiltModuleBuilder<Node, TypeName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>.addProvider(
    name: String,
    returnType: DaggerType<TypeName, AnnotationSpec>,
    isPublic: Boolean,
    mode: ProviderMode<AnnotationSpec> = ProviderMode.Single,
    contents: ProviderContext.() -> CodeBlock,
) = addProvider(name, returnType, isPublic, emptyList(), mode) { contents() }

fun <Node, TypeName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile> HiltModuleBuilder<Node, TypeName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>.addProvider(
    name: String,
    returnType: DaggerType<TypeName, AnnotationSpec>,
    isPublic: Boolean,
    parameter: Parameter<TypeName, AnnotationSpec>,
    mode: ProviderMode<AnnotationSpec> = ProviderMode.Single,
    contents: ProviderContext.(parameter: ParameterSpec) -> CodeBlock,
) = addProvider(name, returnType, isPublic, listOf(parameter), mode) { contents(it.single()) }

fun <AnnotationSpec> ProviderMode<AnnotationSpec>.asAnnotations(
    annotationSpecFromAnnotation: (KClass<out Annotation>) -> AnnotationSpec,
) =
    when (this) {
        ProviderMode.Single -> listOf()
        ProviderMode.IntoSet -> listOf(annotationSpecFromAnnotation(IntoSet::class))
        is ProviderMode.IntoMap -> listOf(annotationSpecFromAnnotation(IntoMap::class), bindingKey)
    }

fun <TypeName> Parameter<TypeName, *>.asParameterName(simpleName: TypeName.() -> String): String =
    when (this) {
        is HiltModuleBuilder.Lazy -> "lazy${type.asParameterName(simpleName).replaceFirstChar(Char::uppercaseChar)}"
        is HiltModuleBuilder.Provider -> "${
            type.asParameterName(simpleName).replaceFirstChar(Char::lowercaseChar)
        }Provider"

        is DaggerType -> type.simpleName().replaceFirstChar(Char::lowercaseChar)
    }

fun <TypeName> Parameter<TypeName, *>.asTypeName(
    parameterizedTypeName: (rawType: KClass<*>, arguments: List<TypeName>) -> TypeName,
): TypeName = when (this) {
    is HiltModuleBuilder.Lazy -> parameterizedTypeName(lazy, listOf(type.asTypeName(parameterizedTypeName)))
    is HiltModuleBuilder.Provider -> parameterizedTypeName(provider, listOf(type.asTypeName(parameterizedTypeName)))
    is DaggerType -> type
}

private val lazy = Lazy::class
private val provider = Provider::class