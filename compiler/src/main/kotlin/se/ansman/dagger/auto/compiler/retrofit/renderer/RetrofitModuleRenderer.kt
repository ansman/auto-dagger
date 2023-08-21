package se.ansman.dagger.auto.compiler.retrofit.renderer

import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.Renderer
import se.ansman.dagger.auto.compiler.retrofit.models.RetrofitModule

abstract class RetrofitModuleRenderer<Node, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>(
    private val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec>,
    private val builderFactory: HiltModuleBuilder.Factory<Node, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>,
) : Renderer<RetrofitModule<Node, ClassName, AnnotationSpec>, SourceFile> {

    final override fun render(input: RetrofitModule<Node, ClassName, AnnotationSpec>): SourceFile =
        builderFactory
            .create(input)
            .addProvider(
                name = "provide${renderEngine.simpleName(input.serviceClass)}",
                parameters = listOf(
                    HiltModuleBuilder.DaggerType(
                        renderEngine.className("retrofit2.Retrofit"),
                        input.qualifiers,
                    )
                ),
                returnType = HiltModuleBuilder.DaggerType(input.serviceClass, setOfNotNull(input.scope)),
                isPublic = input.isPublic,
            ) { parameters -> provideService(input.serviceClass, parameters.single()) }
            .build()

    protected abstract fun provideService(serviceClass: ClassName, retrofitParameter: ParameterSpec): CodeBlock
}