package se.ansman.dagger.auto.compiler.plugin.ir.model

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import se.ansman.dagger.auto.compiler.common.processing.model.ValueParameter
import se.ansman.dagger.auto.compiler.plugin.ir.KirEnvironment

data class KirValueParameter(
    override val environment: KirEnvironment,
    override val node: IrValueParameter
) : KirNode(), ValueParameter<IrElement, KirTypeName, KirClassName> {
    override val name: String
        get() = node.name.asString()

    override val type: KirType by lazy(LazyThreadSafetyMode.NONE) {
        node.type.toKirType()
    }
}