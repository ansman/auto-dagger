package se.ansman.dagger.auto.renderers

import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.Initializable
import se.ansman.dagger.auto.models.AutoInitializeModule
import se.ansman.dagger.auto.models.AutoInitializeObject
import kotlin.reflect.KClass

abstract class AutoInitializeModuleRenderer<Node, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>(
    private val createBuilder: (
        moduleName: ClassName,
        installInComponent: ClassName,
        originatingTopLevelClassName: ClassName
    ) -> HiltModuleBuilder<Node, TypeName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>
) : Renderer<AutoInitializeModule<Node, TypeName, ClassName, AnnotationSpec>, SourceFile> {

    final override fun render(input: AutoInitializeModule<Node, TypeName, ClassName, AnnotationSpec>): SourceFile =
        createBuilder(input.moduleName, createClassName(singletonComponent), input.topLevelClassName)
            .apply {
                for (obj in input.objects) {
                    when (obj.method) {
                        is AutoInitializeObject.Method.Provider -> {
                            addProvider(
                                name = obj.method.name,
                                parameters = listOf(HiltModuleBuilder.Lazy(obj.targetType, obj.qualifiers)),
                                mode = HiltModuleBuilder.ProviderMode.IntoSet,
                                returnType = HiltModuleBuilder.DaggerType(createClassName(initializable)),
                                isPublic = obj.isPublic,
                                originatingElement = obj.originatingElement,
                            ) { (parameter) -> obj.createProviderCode(parameter) }
                        }

                        is AutoInitializeObject.Method.Binding -> addBinding(
                            name = obj.method.name,
                            sourceType = HiltModuleBuilder.DaggerType(obj.targetType, obj.qualifiers),
                            mode = HiltModuleBuilder.ProviderMode.IntoSet,
                            returnType = HiltModuleBuilder.DaggerType(createClassName(initializable)),
                            isPublic = obj.isPublic,
                            originatingElement = obj.originatingElement
                        )
                    }
                }

            }
            .build()

    abstract fun AutoInitializeObject<Node, TypeName, AnnotationSpec>.createProviderCode(parameter: ParameterSpec): CodeBlock
    abstract fun createClassName(type: KClass<*>): ClassName

    companion object {
        private val singletonComponent = SingletonComponent::class
        private val initializable = Initializable::class
    }
}