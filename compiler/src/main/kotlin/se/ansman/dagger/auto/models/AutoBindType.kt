package se.ansman.dagger.auto.models

import se.ansman.dagger.auto.renderers.HiltModuleBuilder

data class AutoBindType<TypeName, AnnotationSpec>(
    val type: TypeName,
    val mode: HiltModuleBuilder.ProviderMode<AnnotationSpec>,
)