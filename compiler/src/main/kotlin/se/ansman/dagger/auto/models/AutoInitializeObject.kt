package se.ansman.dagger.auto.models

data class AutoInitializeObject<out Node, out TypeName, out AnnotationSpec>(
    val targetType: TypeName,
    val priority: Int?,
    val isPublic: Boolean,
    val method: Method,
    val originatingElement: Node,
    val qualifiers: Set<AnnotationSpec> = emptySet(),
) {
    sealed class Method {
        abstract val name: String

        data class Binding(override val name: String) : Method() {
            companion object {
                fun fromType(className: String) = Binding("bind${className}AsInitializable")
            }
        }

        data class Provider(override val name: String) : Method() {
            companion object {
                fun fromType(className: String) = Provider("provide${className}AsInitializable")
            }
        }
    }
}
