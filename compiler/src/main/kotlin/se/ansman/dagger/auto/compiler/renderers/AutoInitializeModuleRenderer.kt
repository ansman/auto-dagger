package se.ansman.dagger.auto.compiler.renderers

import se.ansman.dagger.auto.Initializable
import se.ansman.dagger.auto.compiler.applyEach
import se.ansman.dagger.auto.compiler.models.AutoInitializeModule
import se.ansman.dagger.auto.compiler.processing.RenderEngine

abstract class AutoInitializeModuleRenderer<Node, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>(
    private val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec>,
    private val builderFactory: HiltModuleBuilder.Factory<Node, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>,
) : Renderer<AutoInitializeModule<Node, TypeName, ClassName, AnnotationSpec>, SourceFile> {

    final override fun render(input: AutoInitializeModule<Node, TypeName, ClassName, AnnotationSpec>): SourceFile =
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
                        parameters = listOf(HiltModuleBuilder.DaggerType(obj.targetType, obj.qualifiers)),
                        mode = HiltModuleBuilder.ProviderMode.IntoSet,
                        returnType = HiltModuleBuilder.DaggerType(renderEngine.className(initializable)),
                        isPublic = obj.isPublic,
                    ) { (parameter) -> wrapInitializable(parameter, obj.priority) }

                    else -> addProvider(
                        name = "provide${renderEngine.simpleName(obj.targetType)}AsInitializable",
                        parameters = listOf(HiltModuleBuilder.Lazy(obj.targetType, obj.qualifiers)),
                        mode = HiltModuleBuilder.ProviderMode.IntoSet,
                        returnType = HiltModuleBuilder.DaggerType(renderEngine.className(initializable)),
                        isPublic = obj.isPublic,
                    ) { (parameter) -> createInitializableFromLazy(parameter, obj.priority) }
                }
            }
            .build()

    abstract fun createInitializableFromLazy(parameter: ParameterSpec, priority: Int?): CodeBlock
    abstract fun wrapInitializable(parameter: ParameterSpec, priority: Int): CodeBlock

    companion object {
        private val initializable = Initializable::class
    }
}