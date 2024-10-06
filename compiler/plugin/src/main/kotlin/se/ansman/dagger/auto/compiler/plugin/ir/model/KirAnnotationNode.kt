package se.ansman.dagger.auto.compiler.plugin.ir.model

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrClassReference
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrGetEnumValue
import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.getValueArgument
import org.jetbrains.kotlin.name.Name
import se.ansman.dagger.auto.compiler.common.processing.model.AnnotationNode
import se.ansman.dagger.auto.compiler.common.processing.model.ClassDeclaration
import se.ansman.dagger.auto.compiler.plugin.ir.KirEnvironment
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

data class KirAnnotationNode(
    override val node: IrConstructorCall,
    override val environment: KirEnvironment,
) : KirNode(), AnnotationNode<IrElement, KirTypeName, KirClassName> {
    override val declaration: KirClassDeclaration by lazy(LazyThreadSafetyMode.NONE) {
        KirClassDeclaration(node.type.getClass()!!, environment)
    }

    override fun <T : Any> getValue(type: Type, name: String): T? {
        val value = node.getValueArgument(Name.identifier(name)) ?: return null
        @Suppress("UNCHECKED_CAST")
        return value.toValue(type) as T
    }

    private fun IrElement.toValue(type: Type): Any =
        when {
            type is Class<*> && type in primitives -> (this as IrConst<*>).value!!
            type is Class<*> && type.isEnum -> {
                val name = (this as IrGetEnumValue).symbol.owner.name.asString()
                type.enumConstants
                    .asSequence()
                    .filterIsInstance<Enum<*>>()
                    .first { it.name == name }
            }

            type is ParameterizedType && type.rawType == ClassDeclaration::class.java -> {
                val classReference = this as IrClassReference
                KirClassDeclaration(classReference.symbol.owner as IrClass, environment)
            }

            type is ParameterizedType && type.rawType == List::class.java -> {
                val list = this as IrVararg
                list.elements.map { it.toValue(type.actualTypeArguments.single()) }
            }

            else -> throw UnsupportedOperationException("Annotation value of type $type isn't supported")
        }

    companion object {
        private val primitives = setOf(
            String::class.java,
            Boolean::class.java,
            Char::class.java,
            Short::class.java,
            Int::class.java,
            Long::class.java,
            Float::class.java,
            Double::class.java,
        )
    }
}