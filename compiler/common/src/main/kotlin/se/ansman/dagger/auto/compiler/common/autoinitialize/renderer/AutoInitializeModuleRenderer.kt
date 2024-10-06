package se.ansman.dagger.auto.compiler.common.autoinitialize.renderer

import se.ansman.dagger.auto.compiler.common.applyEach
import se.ansman.dagger.auto.compiler.common.autoinitialize.models.AutoInitializeModule
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.Renderer
import se.ansman.dagger.auto.compiler.common.rendering.addProvider

abstract class AutoInitializeModuleRenderer<N, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>(
    private val renderEngine: RenderEngine<N, TypeName, ClassName, AnnotationSpec>,
    private val builderFactory: HiltModuleBuilder.Factory<N, TypeName, ClassName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>,
) : Renderer<AutoInitializeModule<N, TypeName, ClassName, AnnotationSpec>, SourceFile> {

    final override fun render(input: AutoInitializeModule<N, TypeName, ClassName, AnnotationSpec>): SourceFile =
        builderFactory
            .create(input)
            .applyEach(input.objects) { obj ->
                when {
                    obj.isInitializable && obj.priority == null -> addBinding(
                        name = "bind${renderEngine.simpleName(obj.targetType)}AsInitializable",
                        sourceType = HiltModuleBuilder.DaggerType(obj.targetType, obj.qualifiers),
                        mode = HiltModuleBuilder.ProviderMode.IntoSet,
                        returnType = HiltModuleBuilder.DaggerType(renderEngine.className(initializable)),
                        isPublic = obj.isPublic,
                    )

                    obj.isInitializable && obj.priority != null -> addProvider(
                        name = "provide${renderEngine.simpleName(obj.targetType)}AsInitializable",
                        parameter = HiltModuleBuilder.DaggerType(obj.targetType, obj.qualifiers),
                        mode = HiltModuleBuilder.ProviderMode.IntoSet,
                        returnType = HiltModuleBuilder.DaggerType(renderEngine.className(initializable)),
                        isPublic = obj.isPublic,
                    ) { parameter -> wrapInitializable(parameter, obj.priority) }

                    else -> addProvider(
                        name = "provide${renderEngine.simpleName(obj.targetType)}AsInitializable",
                        parameter = HiltModuleBuilder.Lazy(obj.targetType, obj.qualifiers),
                        mode = HiltModuleBuilder.ProviderMode.IntoSet,
                        returnType = HiltModuleBuilder.DaggerType(renderEngine.className(initializable)),
                        isPublic = obj.isPublic,
                    ) { parameter -> createInitializableFromLazy(parameter, obj.priority) }
                }
            }
            .build()

    abstract fun ProviderContext.createInitializableFromLazy(parameter: ParameterSpec, priority: Int?): CodeBlock
    abstract fun ProviderContext.wrapInitializable(parameter: ParameterSpec, priority: Int): CodeBlock

    companion object {
        private val initializable = se.ansman.dagger.auto.Initializable::class
    }
}