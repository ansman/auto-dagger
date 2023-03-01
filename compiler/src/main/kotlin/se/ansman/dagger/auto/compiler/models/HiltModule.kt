package se.ansman.dagger.auto.compiler.models

import se.ansman.dagger.auto.compiler.renderers.HiltModuleBuilder

interface HiltModule<out Node, ClassName> {
    val moduleName: ClassName
    val installation: HiltModuleBuilder.Installation<ClassName>
    val originatingTopLevelClassName: ClassName
    val originatingElement: Node
}