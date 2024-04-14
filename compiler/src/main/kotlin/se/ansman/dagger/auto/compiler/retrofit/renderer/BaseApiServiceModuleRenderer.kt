package se.ansman.dagger.auto.compiler.retrofit.renderer

import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.Renderer
import se.ansman.dagger.auto.compiler.retrofit.models.ApiServiceModule

abstract class BaseApiServiceModuleRenderer<Node, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>(
    protected val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec>,
    private val builderFactory: HiltModuleBuilder.Factory<Node, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>,
) : Renderer<ApiServiceModule<Node, ClassName, AnnotationSpec>, SourceFile> {
    protected abstract val serviceFactory: ClassName

    final override fun render(input: ApiServiceModule<Node, ClassName, AnnotationSpec>): SourceFile =
        builderFactory
            .create(input)
            .addProvider(
                name = "provide${renderEngine.simpleName(input.serviceClass)}",
                parameters = listOf(HiltModuleBuilder.DaggerType(serviceFactory, input.qualifiers)),
                returnType = HiltModuleBuilder.DaggerType(input.serviceClass, setOfNotNull(input.scope)),
                isPublic = input.isPublic,
            ) { parameters -> provideService(input.serviceClass, parameters.single()) }
            .build()

    protected abstract fun provideService(serviceClass: ClassName, serviceFactoryParameter: ParameterSpec): CodeBlock
}