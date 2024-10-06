package se.ansman.dagger.auto.compiler.plugin.autoinitialize.renderer

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.fields
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.toIrConst
import se.ansman.dagger.auto.Initializable
import se.ansman.dagger.auto.compiler.common.autoinitialize.renderer.AutoInitializeModuleRenderer
import se.ansman.dagger.auto.compiler.plugin.ir.HiltIrModuleBuilder
import se.ansman.dagger.auto.compiler.plugin.ir.KirCodeBlock
import se.ansman.dagger.auto.compiler.plugin.ir.KirEnvironment
import se.ansman.dagger.auto.compiler.plugin.ir.KirClass
import se.ansman.dagger.auto.compiler.plugin.ir.KirParameterSpec
import se.ansman.dagger.auto.compiler.plugin.ir.dumpSrc
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirAnnotationSpec
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirClassName
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirTypeName

class KirAutoInitializeObjectRenderer(
    val environment: KirEnvironment
) : AutoInitializeModuleRenderer<IrElement, KirTypeName, KirClassName, KirAnnotationSpec, KirParameterSpec, IrFunction, KirCodeBlock, KirClass>(
    environment,
    HiltIrModuleBuilder.Factory(environment)
) {

    private val initializable by lazy(LazyThreadSafetyMode.NONE) {
        environment.lookupType(Initializable::class).node
    }

    private val initializableCompanion by lazy(LazyThreadSafetyMode.NONE) {
        environment.lookupType(Initializable.Companion::class).node
    }

    private val initializableCompanionField by lazy(LazyThreadSafetyMode.NONE) {
        initializable.declarations.forEach {
            environment.logger.info(it.dump())
            environment.logger.info(it.dumpSrc())
        }
        initializableCompanion.declarations.forEach {
            environment.logger.info(it.dump())
            environment.logger.info(it.dumpSrc())
        }
        initializable.fields.single { it.origin == IrDeclarationOrigin.FIELD_FOR_OBJECT_INSTANCE }
    }

    private val asInitializable by lazy(LazyThreadSafetyMode.NONE) {
        initializableCompanion.getSimpleFunction("asInitializable")!!
    }

    private val withPriority by lazy(LazyThreadSafetyMode.NONE) {
        initializableCompanion.getSimpleFunction("withPriority")!!
    }

    override fun IrFunction.createInitializableFromLazy(parameter: KirParameterSpec, priority: Int?): KirCodeBlock =
        environment.renderEngine.buildIrDeclaration(symbol) {
            irExprBody(
                irCall(asInitializable).apply {
                    dispatchReceiver = irGetObject(initializableCompanion.symbol)
                    extensionReceiver = irGet(parameter.symbol.owner)
                    if (priority != null) {
                        putValueArgument(0, priority.toIrConst(context.irBuiltIns.intType))
                    }
                }
            )
        }

    override fun IrFunction.wrapInitializable(parameter: KirParameterSpec, priority: Int): KirCodeBlock =
        environment.renderEngine.buildIrDeclaration(symbol) {
            irExprBody(
                irCall(withPriority).apply {
                    dispatchReceiver = irGetObject(initializableCompanion.symbol)
                    extensionReceiver = irGet(parameter.symbol.owner)
                    putValueArgument(0, priority.toIrConst(context.irBuiltIns.intType))
                }
            )
        }
}