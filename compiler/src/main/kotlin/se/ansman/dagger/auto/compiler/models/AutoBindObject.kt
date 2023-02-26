package se.ansman.dagger.auto.compiler.models

data class AutoBindObject<out Node, TypeName, ClassName : TypeName, AnnotationSpec>(
    val sourceType: ClassName,
    val targetComponent: ClassName,
    val isPublic: Boolean,
    val boundTypes: List<AutoBindType<TypeName, AnnotationSpec>>,
    val qualifiers: Set<AnnotationSpec> = emptySet(),
    val originatingElement: Node,
    val originatingTopLevelClassName: ClassName,
) {
    fun withTypesAdded(
        types: List<AutoBindType<TypeName, AnnotationSpec>>
    ): AutoBindObject<Node, TypeName, ClassName, AnnotationSpec> =
        copy(boundTypes = boundTypes + types)
}