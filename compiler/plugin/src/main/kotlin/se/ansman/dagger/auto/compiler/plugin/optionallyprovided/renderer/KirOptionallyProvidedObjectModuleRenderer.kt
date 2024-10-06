package se.ansman.dagger.auto.compiler.plugin.optionallyprovided.renderer

import org.jetbrains.kotlin.ir.IrElement
import se.ansman.dagger.auto.compiler.common.optionallyprovided.renderer.OptionallyProvidedObjectModuleRenderer
import se.ansman.dagger.auto.compiler.plugin.ir.HiltIrModuleBuilder
import se.ansman.dagger.auto.compiler.plugin.ir.KirEnvironment
import se.ansman.dagger.auto.compiler.plugin.ir.KirClass
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirAnnotationSpec
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirClassName
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirTypeName

class KirOptionallyProvidedObjectModuleRenderer(
    environment: KirEnvironment
) : OptionallyProvidedObjectModuleRenderer<IrElement, KirTypeName, KirClassName, KirAnnotationSpec, KirClass>(
    environment,
    HiltIrModuleBuilder.Factory(environment)
)