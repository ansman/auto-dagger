package se.ansman.dagger.auto.compiler.common.ksp.processing

import com.google.devtools.ksp.isDefault
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueArgument
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import se.ansman.dagger.auto.compiler.common.ksp.toAnnotationSpecFixed
import se.ansman.dagger.auto.compiler.common.processing.AnnotationModel
import kotlin.reflect.KClass

class KspAnnotationModel(
    private val annotation: KSAnnotation,
    private val resolver: KspResolver,
) : AnnotationModel<ClassName, AnnotationSpec> {
    private val className by lazy(LazyThreadSafetyMode.NONE) {
        annotation.annotationType.resolve().toClassName()
    }

    override val qualifiedName: String
        get() = className.canonicalName

    override val declaredAnnotations: List<KspAnnotationModel> by lazy(LazyThreadSafetyMode.NONE) {
        annotation.annotationType.resolve().declaration.annotations.map { KspAnnotationModel(it, resolver) }.toList()
    }

    override fun isOfType(type: KClass<out Annotation>): Boolean =
        annotation.shortName.asString() == type.simpleName && type.asClassName() == className

    override fun isOfType(type: String): Boolean =
        type.endsWith(annotation.shortName.asString()) && type == qualifiedName

    override fun <T : Any> getValue(type: Class<T>, name: String): T? =
        annotation.arguments.find { it.name?.asString() == name }
            ?.takeUnless { it.isDefault() }
            ?.convertedValue
            ?.let { value ->
                if (type.isEnum) {
                    val enumEntry = (value as KspClassDeclaration)
                        .node
                        .simpleName
                        .asString()

                    type.enumConstants
                        .first { (it as Enum<*>).name == enumEntry }!!
                        .let(type::cast)
                } else {
                    type.cast(value)
                }
            }

    override fun toAnnotationSpec(): AnnotationSpec = annotation.toAnnotationSpecFixed()

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
                else -> throw UnsupportedOperationException("Annotation value of type $javaClass isn't supported")
            }
            return value?.convert()
        }
}