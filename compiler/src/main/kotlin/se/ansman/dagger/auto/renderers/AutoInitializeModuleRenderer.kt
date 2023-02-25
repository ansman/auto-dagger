package se.ansman.dagger.auto.renderers

import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.Initializable
import se.ansman.dagger.auto.models.AutoInitializeModule
import se.ansman.dagger.auto.processing.RenderEngine

abstract class AutoInitializeModuleRenderer<Node, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>(
    private val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec>,
    private val createBuilder: (
        moduleName: ClassName,
        installInComponent: ClassName,
        originatingTopLevelClassName: ClassName,
    ) -> HiltModuleBuilder<Node, TypeName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>,
) : Renderer<AutoInitializeModule<Node, TypeName, ClassName, AnnotationSpec>, SourceFile> {

    final override fun render(input: AutoInitializeModule<Node, TypeName, ClassName, AnnotationSpec>): SourceFile =
        createBuilder(input.moduleName, renderEngine.className(singletonComponent), input.topLevelClassName)
            .apply {
                for (obj in input.objects) {
                    when {
                        obj.isInitializable && obj.priority == null -> addBinding(
                            name = "bind${renderEngine.simpleName(renderEngine.rawType(obj.targetType))}AsInitializable",
                            sourceType = HiltModuleBuilder.DaggerType(obj.targetType, obj.qualifiers),
                            mode = HiltModuleBuilder.ProviderMode.IntoSet,
                            returnType = HiltModuleBuilder.DaggerType(renderEngine.className(initializable)),
                            isPublic = obj.isPublic,
                            originatingElement = obj.originatingElement
                        )

                        obj.isInitializable && obj.priority != null -> addProvider(
                            name = "provide${renderEngine.simpleName(renderEngine.rawType(obj.targetType))}AsInitializable",
                            parameters = listOf(HiltModuleBuilder.DaggerType(obj.targetType, obj.qualifiers)),
                            mode = HiltModuleBuilder.ProviderMode.IntoSet,
                            returnType = HiltModuleBuilder.DaggerType(renderEngine.className(initializable)),
                            isPublic = obj.isPublic,
                            originatingElement = obj.originatingElement,
                        ) { (parameter) -> wrapInitializable(parameter, obj.priority) }

                        else -> addProvider(
                            name = "provide${renderEngine.simpleName(renderEngine.rawType(obj.targetType))}AsInitializable",
                            parameters = listOf(HiltModuleBuilder.Lazy(obj.targetType, obj.qualifiers)),
                            mode = HiltModuleBuilder.ProviderMode.IntoSet,
                            returnType = HiltModuleBuilder.DaggerType(renderEngine.className(initializable)),
                            isPublic = obj.isPublic,
                            originatingElement = obj.originatingElement,
                        ) { (parameter) -> createInitializableFromLazy(parameter, obj.priority) }
                    }
                }
            }
            .build()

    abstract fun createInitializableFromLazy(parameter: ParameterSpec, priority: Int?): CodeBlock
    abstract fun wrapInitializable(parameter: ParameterSpec, priority: Int): CodeBlock

    companion object {
        private val singletonComponent = SingletonComponent::class
        private val initializable = Initializable::class
    }
}