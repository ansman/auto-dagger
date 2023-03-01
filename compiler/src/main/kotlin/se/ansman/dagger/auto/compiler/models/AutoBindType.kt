package se.ansman.dagger.auto.compiler.models

import se.ansman.dagger.auto.compiler.renderers.HiltModuleBuilder

data class AutoBindType<TypeName, AnnotationSpec>(
    val type: TypeName,
    val mode: HiltModuleBuilder.ProviderMode<AnnotationSpec>,
)