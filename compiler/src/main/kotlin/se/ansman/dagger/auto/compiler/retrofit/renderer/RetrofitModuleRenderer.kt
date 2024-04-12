package se.ansman.dagger.auto.compiler.retrofit.renderer

import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder

abstract class RetrofitModuleRenderer<Node, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>(
    renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec>,
    builderFactory: HiltModuleBuilder.Factory<Node, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>,
) : BaseApiServiceModuleRenderer<Node, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>(
    renderEngine,
    builderFactory
) {
    override val serviceFactory: ClassName
        get() = renderEngine.className("retrofit2.Retrofit")
}