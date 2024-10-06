package se.ansman.dagger.auto.compiler.plugin.autobind.renderer

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.declarations.IrFunction
import se.ansman.dagger.auto.compiler.common.autobind.AutoBindObjectModuleRenderer
import se.ansman.dagger.auto.compiler.plugin.ir.HiltIrModuleBuilder
import se.ansman.dagger.auto.compiler.plugin.ir.KirCodeBlock
import se.ansman.dagger.auto.compiler.plugin.ir.KirEnvironment
import se.ansman.dagger.auto.compiler.plugin.ir.KirClass
import se.ansman.dagger.auto.compiler.plugin.ir.KirParameterSpec
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirAnnotationSpec
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirClassName
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirTypeName

class KirAutoBindObjectModuleRenderer(
    private val environment: KirEnvironment
) : AutoBindObjectModuleRenderer<IrElement, KirTypeName, KirClassName, KirAnnotationSpec, KirParameterSpec, IrFunction, KirCodeBlock, KirClass>(
    environment,
    HiltIrModuleBuilder.Factory(environment)
) {
    override fun IrFunction.provideObject(type: KirClassName): KirCodeBlock =
        environment.renderEngine.buildIrDeclaration(symbol) {
            irExprBody(
                irGetObject(environment.lookupType(type).node.symbol)
            )
        }
}