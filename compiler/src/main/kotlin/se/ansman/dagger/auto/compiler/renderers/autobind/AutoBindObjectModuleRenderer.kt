package se.ansman.dagger.auto.compiler.renderers.autobind

import se.ansman.dagger.auto.compiler.common.applyEach
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.Renderer
import se.ansman.dagger.auto.compiler.models.autobind.AutoBindObjectModule

abstract class AutoBindObjectModuleRenderer<Node, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>(
    private val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec>,
    private val builderFactory: HiltModuleBuilder.Factory<Node, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>,
) : Renderer<AutoBindObjectModule<Node, TypeName, ClassName, AnnotationSpec>, SourceFile> {

    final override fun render(input: AutoBindObjectModule<Node, TypeName, ClassName, AnnotationSpec>): SourceFile =
        builderFactory
            .create(input)
            .applyEach(input.boundTypes) { boundType ->
                with(renderEngine) {
                    val suffix = buildString {
                        this
                            .append(simpleName(input.type))
                            .append("As")
                            .append(simpleName(boundType.type))
                            .append(boundType.mode.suffix)
                    }
                    val returnType = HiltModuleBuilder.DaggerType(boundType.type, boundType.qualifiers)
                    if (input.isObject) {
                        addProvider(
                            name = "provide$suffix",
                            mode = boundType.mode,
                            returnType = returnType,
                            isPublic = input.isPublic,
                        ) {
                            provideObject(input.type)
                        }
                    } else {
                        addBinding(
                            name = "bind$suffix",
                            sourceType = HiltModuleBuilder.DaggerType(input.type),
                            mode = boundType.mode,
                            returnType = returnType,
                            isPublic = input.isPublic,
                        )
                    }
                }
            }
            .build()

    protected abstract fun provideObject(type: ClassName): CodeBlock

    private val HiltModuleBuilder.ProviderMode<*>.suffix: String
        get() = when (this) {
            HiltModuleBuilder.ProviderMode.Single -> ""
            HiltModuleBuilder.ProviderMode.IntoSet -> "IntoSet"
            is HiltModuleBuilder.ProviderMode.IntoMap -> "IntoMap"
        }
}