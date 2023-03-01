package se.ansman.dagger.auto.compiler.models

data class AutoInitializeObject<out Node, out TypeName, out AnnotationSpec>(
    val targetType: TypeName,
    val priority: Int?,
    val isPublic: Boolean,
    val isInitializable: Boolean,
    val originatingElement: Node,
    val qualifiers: Set<AnnotationSpec> = emptySet(),
)
