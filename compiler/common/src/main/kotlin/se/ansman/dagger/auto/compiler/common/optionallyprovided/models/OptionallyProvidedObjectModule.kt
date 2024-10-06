package se.ansman.dagger.auto.compiler.common.optionallyprovided.models

import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.models.HiltModule
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder

data class OptionallyProvidedObjectModule<N, TypeName, ClassName : TypeName, AnnotationSpec>(
    override val processor: Class<out Processor<*, *, *, *>>,
    override val moduleName: ClassName,
    override val installation: HiltModuleBuilder.Installation<ClassName>,
    override val originatingTopLevelClassName: ClassName,
    override val originatingElement: N,
    val type: ClassName,
    val isPublic: Boolean,
    val qualifiers: Set<AnnotationSpec>,
) : HiltModule<N, ClassName>