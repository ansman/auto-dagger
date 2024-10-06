package se.ansman.dagger.auto.compiler.common.retrofit.renderer

import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.Renderer
import se.ansman.dagger.auto.compiler.common.rendering.addProvider
import se.ansman.dagger.auto.compiler.common.retrofit.models.ApiServiceModule

abstract class BaseApiServiceModuleRenderer<N, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>(
    protected val renderEngine: RenderEngine<N, TypeName, ClassName, AnnotationSpec>,
    private val builderFactory: HiltModuleBuilder.Factory<N, TypeName, ClassName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>,
) : Renderer<ApiServiceModule<N, ClassName, AnnotationSpec>, SourceFile> {
    protected abstract val serviceFactory: ClassName

    final override fun render(input: ApiServiceModule<N, ClassName, AnnotationSpec>): SourceFile =
        builderFactory
            .create(input)
            .addProvider(
                name = "provide${renderEngine.simpleName(input.serviceClass)}",
                parameter = HiltModuleBuilder.DaggerType(serviceFactory, input.qualifiers),
                returnType = HiltModuleBuilder.DaggerType(input.serviceClass, setOfNotNull(input.scope)),
                isPublic = input.isPublic,
            ) { parameter -> provideService(input.serviceClass, parameter) }
            .build()

    protected abstract fun ProviderContext.provideService(serviceClass: ClassName, serviceFactoryParameter: ParameterSpec): CodeBlock
}