package se.ansman.dagger.auto.compiler.renderers

import se.ansman.dagger.auto.compiler.applyEach
import se.ansman.dagger.auto.compiler.deleteSuffix
import se.ansman.dagger.auto.compiler.models.AutoBindObject
import se.ansman.dagger.auto.compiler.processing.RenderEngine
import se.ansman.dagger.auto.compiler.processing.rootPeerClass

abstract class AutoBindModuleRenderer<Node, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>(
    private val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec>,
    private val createBuilder: (
        moduleName: ClassName,
        installInComponent: ClassName,
        originatingTopLevelClassName: ClassName,
    ) -> HiltModuleBuilder<Node, TypeName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>,
) : Renderer<AutoBindObject<Node, TypeName, ClassName, AnnotationSpec>, SourceFile> {

    final override fun render(input: AutoBindObject<Node, TypeName, ClassName, AnnotationSpec>): SourceFile =
        with(renderEngine) {
            createBuilder(input.moduleName, input.targetComponent, input.originatingTopLevelClassName)
                .applyEach(input.boundTypes) { obj ->
                    val suffix = when (obj.mode) {
                        HiltModuleBuilder.ProviderMode.Single -> ""
                        HiltModuleBuilder.ProviderMode.IntoSet -> "IntoSet"
                        is HiltModuleBuilder.ProviderMode.IntoMap -> "IntoMap"
                    }
                    addBinding(
                        name = "bind${simpleName(input.sourceType)}As${simpleName(rawType(obj.type))}$suffix",
                        sourceType = HiltModuleBuilder.DaggerType(input.sourceType, input.qualifiers),
                        mode = obj.mode,
                        returnType = HiltModuleBuilder.DaggerType(obj.type, input.qualifiers),
                        isPublic = input.isPublic,
                        originatingElement = input.originatingElement
                    )
                }
                .build()
        }

    private val AutoBindObject<Node, TypeName, ClassName, AnnotationSpec>.moduleName: ClassName
        get() = with(renderEngine) {
            rootPeerClass(sourceType, buildString {
                append("AutoBind")
                simpleNames(sourceType).joinTo(this, "")
                simpleNames(targetComponent).joinTo(this, "")
                deleteSuffix("Component")
                append("Module")
            })
        }
}