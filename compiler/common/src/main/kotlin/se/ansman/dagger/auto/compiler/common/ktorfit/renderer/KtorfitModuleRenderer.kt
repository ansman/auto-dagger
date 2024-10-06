package se.ansman.dagger.auto.compiler.common.ktorfit.renderer

import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.retrofit.renderer.BaseApiServiceModuleRenderer

abstract class KtorfitModuleRenderer<N, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>(
    renderEngine: RenderEngine<N, TypeName, ClassName, AnnotationSpec>,
    builderFactory: HiltModuleBuilder.Factory<N, TypeName, ClassName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>,
) : BaseApiServiceModuleRenderer<N, TypeName, ClassName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>(
    renderEngine,
    builderFactory
) {
    override val serviceFactory: ClassName
        get() = renderEngine.className("de.jensklingenberg.ktorfit.Ktorfit")
}