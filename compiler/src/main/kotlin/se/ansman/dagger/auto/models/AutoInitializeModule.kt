package se.ansman.dagger.auto.models

data class AutoInitializeModule<out Node, TypeName, ClassName : TypeName, AnnotationSpec>(
    val moduleName: ClassName,
    val objects: List<AutoInitializeObject<Node, TypeName, AnnotationSpec>>,
    val topLevelClassName: ClassName,
)