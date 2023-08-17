package se.ansman.dagger.auto.compiler.models

import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder

data class AutoBindType<TypeName, AnnotationSpec>(
    val type: TypeName,
    val mode: HiltModuleBuilder.ProviderMode<AnnotationSpec>,
    val qualifiers: Set<AnnotationSpec> = emptySet(),
)