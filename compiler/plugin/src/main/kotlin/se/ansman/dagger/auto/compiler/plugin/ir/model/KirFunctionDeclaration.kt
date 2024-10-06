package se.ansman.dagger.auto.compiler.plugin.ir.model

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import se.ansman.dagger.auto.compiler.common.processing.model.FunctionDeclaration
import se.ansman.dagger.auto.compiler.plugin.ir.KirEnvironment

data class KirFunctionDeclaration(
    override val node: IrFunction,
    override val environment: KirEnvironment,
) : KirExecutableDeclaration(), FunctionDeclaration<IrElement, KirTypeName, KirClassName> {
    override val name: String get() = node.name.asString()
    override val isAbstract: Boolean
        get() = node.body == null

    override val isConstructor: Boolean
        get() = node is IrConstructor

    override val receiver: KirType? by lazy(LazyThreadSafetyMode.NONE) {
        node.extensionReceiverParameter?.type?.toKirType()
    }

    override val returnType: KirType by lazy(LazyThreadSafetyMode.NONE) {
        node.returnType.toKirType()
    }

    override val valueParameters: List<KirValueParameter> by lazy(LazyThreadSafetyMode.NONE) {
        node.valueParameters.map { KirValueParameter(environment, it) }
    }
}