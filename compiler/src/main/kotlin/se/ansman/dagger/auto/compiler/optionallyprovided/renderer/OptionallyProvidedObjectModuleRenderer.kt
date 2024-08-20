package se.ansman.dagger.auto.compiler.optionallyprovided.renderer

import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.Renderer
import se.ansman.dagger.auto.compiler.optionallyprovided.models.OptionallyProvidedObjectModule

abstract class OptionallyProvidedObjectModuleRenderer<Node, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>(
    private val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec>,
    private val builderFactory: HiltModuleBuilder.Factory<Node, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>,
) : Renderer<OptionallyProvidedObjectModule<Node, TypeName, ClassName, AnnotationSpec>, SourceFile> {

    final override fun render(input: OptionallyProvidedObjectModule<Node, TypeName, ClassName, AnnotationSpec>): SourceFile =
        builderFactory
            .create(input)
            .apply {
                with(renderEngine) {
                    addOptionalBinding(
                        name = "bindsOptional${simpleName(input.type)}",
                        type = HiltModuleBuilder.DaggerType(input.type, input.qualifiers),
                        isPublic = input.isPublic,
                    )
                }
            }
            .build()
}

