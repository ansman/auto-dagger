package se.ansman.dagger.auto.compiler.models.autoinitialize

import se.ansman.dagger.auto.compiler.common.models.HiltModule
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder

data class AutoInitializeModule<out Node, TypeName, ClassName : TypeName, AnnotationSpec>(
    override val moduleName: ClassName,
    override val installation: HiltModuleBuilder.Installation<ClassName>,
    override val originatingTopLevelClassName: ClassName,
    override val originatingElement: Node,
    val objects: List<AutoInitializeObject<TypeName, AnnotationSpec>>,
) : HiltModule<Node, ClassName>