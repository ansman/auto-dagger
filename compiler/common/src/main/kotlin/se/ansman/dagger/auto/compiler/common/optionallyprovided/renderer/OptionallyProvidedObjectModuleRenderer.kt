package se.ansman.dagger.auto.compiler.common.optionallyprovided.renderer

import se.ansman.dagger.auto.compiler.common.optionallyprovided.models.OptionallyProvidedObjectModule
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.Renderer

abstract class OptionallyProvidedObjectModuleRenderer<N, TypeName, ClassName : TypeName, AnnotationSpec, SourceFile>(
    private val renderEngine: RenderEngine<N, TypeName, ClassName, AnnotationSpec>,
    private val builderFactory: HiltModuleBuilder.Factory<N, TypeName, ClassName, AnnotationSpec, *, *, *, SourceFile>,
) : Renderer<OptionallyProvidedObjectModule<N, TypeName, ClassName, AnnotationSpec>, SourceFile> {

    final override fun render(input: OptionallyProvidedObjectModule<N, TypeName, ClassName, AnnotationSpec>): SourceFile =
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