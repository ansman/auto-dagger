package se.ansman.dagger.auto.compiler.models

import se.ansman.dagger.auto.compiler.renderers.HiltModuleBuilder

data class AutoBindObjectModule<out Node, TypeName, ClassName : TypeName, AnnotationSpec>(
    override val moduleName: ClassName,
    override val installation: HiltModuleBuilder.Installation<ClassName>,
    override val originatingTopLevelClassName: ClassName,
    override val originatingElement: Node,
    val type: ClassName,
    val isPublic: Boolean,
    val isObject: Boolean,
    val boundTypes: List<AutoBindType<TypeName, AnnotationSpec>>,
    val qualifiers: Set<AnnotationSpec> = emptySet(),
) : HiltModule<Node, ClassName> {
    fun withTypesAdded(types: List<AutoBindType<TypeName, AnnotationSpec>>) =
        copy(boundTypes = boundTypes + types)
}