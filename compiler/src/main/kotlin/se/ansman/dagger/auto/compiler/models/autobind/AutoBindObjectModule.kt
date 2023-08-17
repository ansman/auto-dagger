package se.ansman.dagger.auto.compiler.models.autobind

import se.ansman.dagger.auto.compiler.common.models.HiltModule
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder

data class AutoBindObjectModule<out Node, TypeName, ClassName : TypeName, AnnotationSpec>(
    override val moduleName: ClassName,
    override val installation: HiltModuleBuilder.Installation<ClassName>,
    override val originatingTopLevelClassName: ClassName,
    override val originatingElement: Node,
    val type: ClassName,
    val isPublic: Boolean,
    val isObject: Boolean,
    val boundTypes: List<AutoBindType<TypeName, AnnotationSpec>>,
) : HiltModule<Node, ClassName> {
    fun withTypesAdded(types: List<AutoBindType<TypeName, AnnotationSpec>>) =
        copy(boundTypes = boundTypes + types)
}