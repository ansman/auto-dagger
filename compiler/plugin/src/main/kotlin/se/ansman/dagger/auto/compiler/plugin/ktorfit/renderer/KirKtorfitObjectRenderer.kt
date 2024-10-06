package se.ansman.dagger.auto.compiler.plugin.ktorfit.renderer

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import se.ansman.dagger.auto.compiler.common.ktorfit.renderer.KtorfitModuleRenderer
import se.ansman.dagger.auto.compiler.plugin.ir.HiltIrModuleBuilder
import se.ansman.dagger.auto.compiler.plugin.ir.KirCodeBlock
import se.ansman.dagger.auto.compiler.plugin.ir.KirEnvironment
import se.ansman.dagger.auto.compiler.plugin.ir.KirClass
import se.ansman.dagger.auto.compiler.plugin.ir.KirParameterSpec
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirAnnotationSpec
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirClassName
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirTypeName

class KirKtorfitObjectRenderer(
    private val environment: KirEnvironment,
) : KtorfitModuleRenderer<IrElement, KirTypeName, KirClassName, KirAnnotationSpec, KirParameterSpec, IrFunction, KirCodeBlock, KirClass>(
    environment,
    HiltIrModuleBuilder.Factory(environment)
) {

    private val todo by lazy {
        environment.referenceFunctions(CallableId(FqName("kotlin"), Name.identifier("TODO")))
            .single { it.owner.valueParameters.isEmpty() }
    }

    override fun IrFunction.provideService(
        serviceClass: KirClassName,
        serviceFactoryParameter: KirParameterSpec
    ): KirCodeBlock = environment.renderEngine.buildIrDeclaration(symbol) {
        val implClass = serviceClass.sibling("_${serviceClass.simpleName}Impl").classId
        val impl = environment.context.referenceClass(implClass)
            ?.owner
        if (impl == null) {
            environment.logger.error("Could not find implementation named $implClass for $serviceClass", environment.referenceClass(serviceClass.classId)!!.owner)
            return@buildIrDeclaration irExprBody(irCall(todo))
        }
        val constructor = impl.primaryConstructor ?: run {
            environment.logger.error("Could not find implementation named $implClass for $serviceClass", environment.referenceClass(serviceClass.classId)!!.owner)
            return@buildIrDeclaration irExprBody(irCall(todo))
        }

        irExprBody(
            irCallConstructor(constructor.symbol, emptyList()).apply {
                putValueArgument(0, irGet(serviceFactoryParameter))
            }
        )
    }
}