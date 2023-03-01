package se.ansman.dagger.auto.compiler.renderers

import se.ansman.dagger.auto.compiler.applyEach
import se.ansman.dagger.auto.compiler.models.AutoBindObjectModule
import se.ansman.dagger.auto.compiler.processing.RenderEngine

abstract class AutoBindObjectModuleRenderer<Node, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>(
    private val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec>,
    private val builderFactory: HiltModuleBuilder.Factory<Node, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>,
) : Renderer<AutoBindObjectModule<Node, TypeName, ClassName, AnnotationSpec>, SourceFile> {

    final override fun render(input: AutoBindObjectModule<Node, TypeName, ClassName, AnnotationSpec>): SourceFile =
        builderFactory
            .create(input)
            .applyEach(input.boundTypes) { boundType ->
                with(renderEngine) {
                    addBinding(
                        name = "bind${simpleName(input.type)}As${simpleName(rawType(boundType.type))}${boundType.mode.suffix}",
                        sourceType = HiltModuleBuilder.DaggerType(input.type, input.qualifiers),
                        mode = boundType.mode,
                        returnType = HiltModuleBuilder.DaggerType(boundType.type, input.qualifiers),
                        isPublic = input.isPublic,
                    )
                }
            }
            .build()

    private val HiltModuleBuilder.ProviderMode<*>.suffix: String
        get() = when (this) {
            HiltModuleBuilder.ProviderMode.Single -> ""
            HiltModuleBuilder.ProviderMode.IntoSet -> "IntoSet"
            is HiltModuleBuilder.ProviderMode.IntoMap -> "IntoMap"
        }
}