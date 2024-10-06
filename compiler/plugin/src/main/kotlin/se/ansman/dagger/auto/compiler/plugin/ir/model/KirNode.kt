package se.ansman.dagger.auto.compiler.plugin.ir.model

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import se.ansman.dagger.auto.compiler.common.processing.model.Node
import se.ansman.dagger.auto.compiler.plugin.ir.KirEnvironment

sealed class KirNode : Node<IrElement, KirTypeName, KirClassName> {
    abstract val environment: KirEnvironment

    protected fun IrElement.toKirDeclaration(): KirDeclaration? = toKirDeclaration(environment)
    protected fun IrClass.toKirDeclaration(environment: KirEnvironment): KirDeclaration =
        KirClassDeclaration(this, environment)
    protected fun IrFunction.toKirDeclaration(environment: KirEnvironment): KirDeclaration =
        KirFunctionDeclaration(this, environment)
    protected fun IrProperty.toKirDeclaration(environment: KirEnvironment): KirDeclaration =
        KirPropertyDeclaration(this, environment)

    protected fun IrType.toKirType(): KirType = KirType(this as IrSimpleType, environment)
}

fun IrElement.toKirDeclaration(environment: KirEnvironment): KirDeclaration? = when (this) {
    is IrClass -> KirClassDeclaration(this, environment)
    is IrConstructor -> null // No support for constructors yet
    is IrFunction -> KirFunctionDeclaration(this, environment)
    is IrProperty -> KirPropertyDeclaration(this, environment)
    else -> null
}

fun IrClass.toKirDeclaration(environment: KirEnvironment): KirDeclaration = KirClassDeclaration(this, environment)
fun IrFunction.toKirDeclaration(environment: KirEnvironment): KirDeclaration = KirFunctionDeclaration(this, environment)
fun IrProperty.toKirDeclaration(environment: KirEnvironment): KirDeclaration = KirPropertyDeclaration(this, environment)