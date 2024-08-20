package se.ansman.dagger.auto.compiler.optionallyprovided.models

import se.ansman.dagger.auto.compiler.common.models.HiltModule
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder

data class OptionallyProvidedObjectModule<out Node, TypeName, ClassName : TypeName, AnnotationSpec>(
    override val moduleName: ClassName,
    override val installation: HiltModuleBuilder.Installation<ClassName>,
    override val originatingTopLevelClassName: ClassName,
    override val originatingElement: Node,
    val type: ClassName,
    val isPublic: Boolean,
    val qualifiers: Set<AnnotationSpec>,
) : HiltModule<Node, ClassName>