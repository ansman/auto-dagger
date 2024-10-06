package se.ansman.dagger.auto.compiler.plugin.ir.model

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.isSubtypeOf
import org.jetbrains.kotlin.ir.types.starProjectedType
import se.ansman.dagger.auto.compiler.common.processing.model.Type
import se.ansman.dagger.auto.compiler.plugin.ir.KirEnvironment

data class KirType(
    val type: IrSimpleType,
    private val environment: KirEnvironment
) : Type<IrElement, KirTypeName, KirClassName> {
    override val declaration: KirClassDeclaration? by lazy(LazyThreadSafetyMode.NONE) {
        type.getClass()?.let { KirClassDeclaration(it, environment) }
    }

    override val isGeneric: Boolean
        get() = type.arguments.isNotEmpty()

    override fun toTypeName(): KirTypeName =
        if (type.arguments.isEmpty()) {
            declaration!!.className
        } else {
            KirParameterizedTypeName(
                declaration!!.className,
                type.arguments.map(::KirTypeArgument)
            )
        }

    override fun isAssignableTo(type: String): Boolean =
        isAssignableTo(KirClassName.bestGuess(type))

    override fun isAssignableTo(type: KirClassName): Boolean =
        isAssignableTo(environment.lookupType(type).node.symbol.starProjectedType)

    override fun isAssignableTo(type: Type<IrElement, KirTypeName, KirClassName>): Boolean =
        isAssignableTo((type as KirType).type)

    private fun isAssignableTo(type: IrType): Boolean =
        this.type.isSubtypeOf(type, environment.typeSystem)

}