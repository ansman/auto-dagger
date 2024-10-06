package se.ansman.dagger.auto.compiler.ksp.processing.model

import com.google.devtools.ksp.isDefault
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueArgument
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.model.AnnotationNode
import java.lang.reflect.Type

class KspAnnotationNode(
    override val node: KSAnnotation,
    private val resolver: KspResolver,
) : AnnotationNode<KSNode, TypeName, ClassName> {

    override val declaration: KspClassDeclaration by lazy(LazyThreadSafetyMode.NONE) {
        KspClassDeclaration(node.annotationType.resolve().declaration as KSClassDeclaration, resolver)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getValue(type: Type, name: String): T? =
        node.arguments.find { it.name?.asString() == name }
            ?.takeUnless { it.isDefault() }
            ?.convertedValue
            ?.let { value ->
                if (type is Class<*> && type.isEnum) {
                    val enumEntry = (value as KspClassDeclaration)
                        .node
                        .simpleName
                        .asString()

                    type.enumConstants
                        .first { (it as Enum<*>).name == enumEntry }!!
                        .let(type::cast)
                } else {
                    value
                } as T
            }

    private val KSValueArgument.convertedValue: Any?
        get() {
            fun Any.convert(): Any = when (this) {
                is Boolean,
                is Byte,
                is Char,
                is Short,
                is Int,
                is Long,
                is Float,
                is Double,
                is String -> this

                is List<*> -> map { it!!.convert() }
                is KSType -> KspClassDeclaration(declaration as KSClassDeclaration, resolver)
                is KSClassDeclaration -> KspClassDeclaration(this, resolver)
                else -> throw UnsupportedOperationException("Annotation value of type $javaClass isn't supported")
            }
            return value?.convert()
        }
}