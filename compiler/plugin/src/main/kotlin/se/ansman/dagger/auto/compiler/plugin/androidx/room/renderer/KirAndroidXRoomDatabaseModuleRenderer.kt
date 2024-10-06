package se.ansman.dagger.auto.compiler.plugin.androidx.room.renderer

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.properties
import se.ansman.dagger.auto.compiler.common.androidx.room.AndroidXRoomDatabaseModuleRenderer
import se.ansman.dagger.auto.compiler.common.androidx.room.models.AndroidXRoomDatabaseModule
import se.ansman.dagger.auto.compiler.plugin.ir.HiltIrModuleBuilder
import se.ansman.dagger.auto.compiler.plugin.ir.KirCodeBlock
import se.ansman.dagger.auto.compiler.plugin.ir.KirEnvironment
import se.ansman.dagger.auto.compiler.plugin.ir.KirClass
import se.ansman.dagger.auto.compiler.plugin.ir.KirParameterSpec
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirAnnotationSpec
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirClassName
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirTypeName

class KirAndroidXRoomDatabaseModuleRenderer(
    private val environment: KirEnvironment,
) : AndroidXRoomDatabaseModuleRenderer<IrElement, KirTypeName, KirClassName, KirAnnotationSpec, KirParameterSpec, IrFunction, KirCodeBlock, KirClass>(
    environment.renderEngine,
    HiltIrModuleBuilder.Factory(environment)
) {

    override fun IrFunction.provideDao(
        dao: AndroidXRoomDatabaseModule.Dao<KirTypeName>,
        databaseParameter: KirParameterSpec
    ): KirCodeBlock = environment.renderEngine.buildIrDeclaration(symbol) {
        irExprBody(
            when (dao.accessor) {
                is AndroidXRoomDatabaseModule.Dao.Accessor.Function -> {
                    irCall(databaseParameter.type.classOrFail
                        .owner
                        .functions
                        .map { it }
                        .single { it.name.asString() == dao.accessor.name && it.valueParameters.isEmpty() }
                    ).apply {
                        dispatchReceiver = irGet(databaseParameter)
                    }
                }

                is AndroidXRoomDatabaseModule.Dao.Accessor.Property -> {
                    irCall(databaseParameter.type.classOrFail
                        .owner
                        .properties
                        .single { it.name.asString() == dao.accessor.name }
                        .getter!!
                    ).apply {
                        dispatchReceiver = irGet(databaseParameter)
                    }
                }
            }
        )
    }
}