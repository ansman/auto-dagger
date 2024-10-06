package se.ansman.dagger.auto.compiler.plugin.retrofit.renderer

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.impl.IrClassReferenceImpl
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import se.ansman.dagger.auto.compiler.common.retrofit.renderer.RetrofitModuleRenderer
import se.ansman.dagger.auto.compiler.plugin.ir.HiltIrModuleBuilder
import se.ansman.dagger.auto.compiler.plugin.ir.KirCodeBlock
import se.ansman.dagger.auto.compiler.plugin.ir.KirEnvironment
import se.ansman.dagger.auto.compiler.plugin.ir.KirClass
import se.ansman.dagger.auto.compiler.plugin.ir.KirParameterSpec
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirAnnotationSpec
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirClassName
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirTypeName

class KirRetrofitObjectRenderer(
    private val environment: KirEnvironment
) : RetrofitModuleRenderer<IrElement, KirTypeName, KirClassName, KirAnnotationSpec, KirParameterSpec, IrFunction, KirCodeBlock, KirClass>(
    environment,
    HiltIrModuleBuilder.Factory(environment)
) {
    private val retrofit by lazy {
        environment.lookupType(KirClassName("retrofit2", "Retrofit")).node
    }
    private val create by lazy {
        retrofit.getSimpleFunction("create")!!
    }
    private val javaClassSymbol by lazy {
        environment.renderEngine.typeLookup[ClassId(FqName("java.lang"), Name.identifier("Class"))]
    }

    override fun IrFunction.provideService(
        serviceClass: KirClassName,
        serviceFactoryParameter: KirParameterSpec
    ): KirCodeBlock = environment.renderEngine.buildIrDeclaration(symbol) {
        irExprBody(
            irCall(create).apply {
                dispatchReceiver = irGet(serviceFactoryParameter)
                val type = serviceClass.toIrType(environment)
                putValueArgument(
                    0,
                    IrClassReferenceImpl(
                        startOffset = SYNTHETIC_OFFSET,
                        endOffset = SYNTHETIC_OFFSET,
                        type = javaClassSymbol.typeWith(type),
                        symbol = serviceClass.asClass(environment).symbol,
                        classType = type
                    )
                )

            }
        )
    }
}