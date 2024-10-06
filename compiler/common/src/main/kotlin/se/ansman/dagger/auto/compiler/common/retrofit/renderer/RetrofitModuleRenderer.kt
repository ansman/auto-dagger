package se.ansman.dagger.auto.compiler.common.retrofit.renderer

import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder

abstract class RetrofitModuleRenderer<N, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>(
    renderEngine: RenderEngine<N, TypeName, ClassName, AnnotationSpec>,
    builderFactory: HiltModuleBuilder.Factory<N, TypeName, ClassName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>,
) : BaseApiServiceModuleRenderer<N, TypeName, ClassName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>(
    renderEngine,
    builderFactory
) {
    override val serviceFactory: ClassName
        get() = renderEngine.className("retrofit2.Retrofit")
}