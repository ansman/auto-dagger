package se.ansman.dagger.auto.compiler.common.autobind

import se.ansman.dagger.auto.compiler.common.applyEach
import se.ansman.dagger.auto.compiler.common.autobind.models.AutoBindObjectModule
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.Renderer
import se.ansman.dagger.auto.compiler.common.rendering.addProvider

abstract class AutoBindObjectModuleRenderer<N, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>(
    private val renderEngine: RenderEngine<N, TypeName, ClassName, AnnotationSpec>,
    private val builderFactory: HiltModuleBuilder.Factory<N, TypeName, ClassName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>,
) : Renderer<AutoBindObjectModule<N, TypeName, ClassName, AnnotationSpec>, SourceFile> {

    final override fun render(input: AutoBindObjectModule<N, TypeName, ClassName, AnnotationSpec>): SourceFile =
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

    protected abstract fun ProviderContext.provideObject(type: ClassName): CodeBlock

    private val HiltModuleBuilder.ProviderMode<*>.suffix: String
        get() = when (this) {
            HiltModuleBuilder.ProviderMode.Single -> ""
            HiltModuleBuilder.ProviderMode.IntoSet -> "IntoSet"
            is HiltModuleBuilder.ProviderMode.IntoMap -> "IntoMap"
        }
}