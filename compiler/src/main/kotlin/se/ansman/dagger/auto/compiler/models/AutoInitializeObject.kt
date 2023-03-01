package se.ansman.dagger.auto.compiler.models

data class AutoInitializeObject<out TypeName, out AnnotationSpec>(
    val targetType: TypeName,
    val priority: Int?,
    val isPublic: Boolean,
    val isInitializable: Boolean,
    val qualifiers: Set<AnnotationSpec> = emptySet(),
)
