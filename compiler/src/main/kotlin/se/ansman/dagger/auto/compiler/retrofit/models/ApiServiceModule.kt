package se.ansman.dagger.auto.compiler.retrofit.models

import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.models.HiltModule
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder

data class ApiServiceModule<out Node, ClassName, AnnotationSpec>(
    override val processor: Class<out Processor<*, *, *, *>>,
    override val moduleName: ClassName,
    override val installation: HiltModuleBuilder.Installation<ClassName>,
    override val originatingTopLevelClassName: ClassName,
    override val originatingElement: Node,
    val serviceClass: ClassName,
    val isPublic: Boolean,
    val qualifiers: Set<AnnotationSpec>,
    val scope: AnnotationSpec?,
) : HiltModule<Node, ClassName>