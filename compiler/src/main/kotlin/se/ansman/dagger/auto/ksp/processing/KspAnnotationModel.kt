package se.ansman.dagger.auto.ksp.processing

import com.google.devtools.ksp.isDefault
import com.google.devtools.ksp.symbol.KSAnnotation
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import se.ansman.dagger.auto.ksp.toAnnotationSpecFixed
import se.ansman.dagger.auto.processing.AnnotationModel
import kotlin.reflect.KClass

class KspAnnotationModel(private val annotation: KSAnnotation) : AnnotationModel<ClassName, AnnotationSpec> {
    private val className by lazy(LazyThreadSafetyMode.NONE) {
        annotation.annotationType.resolve().toClassName()
    }

    override val qualifiedName: String
        get() = className.canonicalName

    override val declaredAnnotations: List<KspAnnotationModel> by lazy(LazyThreadSafetyMode.NONE) {
        annotation.annotationType.resolve().declaration.annotations.map(::KspAnnotationModel).toList()
    }

    override fun isOfType(type: KClass<out Annotation>): Boolean =
        annotation.shortName.asString() == type.simpleName && type.asClassName() == className

    override fun isOfType(type: String): Boolean =
        type.endsWith(annotation.shortName.asString()) && type == qualifiedName

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getValue(name: String): T? =
        annotation.arguments.find { it.name?.asString() == name }
            ?.takeUnless { it.isDefault() }
            ?.value as T?

    override fun toAnnotationSpec(): AnnotationSpec = annotation.toAnnotationSpecFixed()
}