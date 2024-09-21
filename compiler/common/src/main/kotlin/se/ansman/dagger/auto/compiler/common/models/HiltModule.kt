package se.ansman.dagger.auto.compiler.common.models

import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder

interface HiltModule<out Node, ClassName> {
    val processor: Class<out Processor<*, *, *, *>>
    val moduleName: ClassName
    val installation: HiltModuleBuilder.Installation<ClassName>
    val originatingTopLevelClassName: ClassName
    val originatingElement: Node
}