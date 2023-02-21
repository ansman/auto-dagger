package se.ansman.dagger.auto.kapt.processing

import com.google.auto.common.MoreTypes
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import se.ansman.dagger.auto.processing.AnnotationModel
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import kotlin.reflect.KClass

class KaptAnnotationModel(private val mirror: AnnotationMirror) : AnnotationModel<ClassName, AnnotationSpec> {
    override val qualifiedName: String
        get() = className.canonicalName()

    private val className by lazy {
        mirror.annotationType.asElement().asType()
            .let(MoreTypes::asTypeElement)
            .let(ClassName::get)
    }
    override val declaredAnnotations: List<KaptAnnotationModel> by lazy {
        mirror.annotationType.asElement().annotationMirrors.map(::KaptAnnotationModel)
    }

    override fun isOfType(type: KClass<out Annotation>): Boolean =
        mirror.annotationType.asElement().simpleName.contentEquals(type.simpleName) &&
                ClassName.get(type.java) == className

    override fun isOfType(type: String): Boolean =
        type.endsWith(mirror.annotationType.asElement().simpleName) && type == className.canonicalName()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getValue(name: String): T? = getValueNamed(name)?.value as T?

//    override fun getClassValue(name: String): ClassName? =
//        (getValueNamed(name)?.value as TypeMirror?)?.asClassName()
//
//    override fun getClassListValue(name: String): List<ClassName> =
//        (getValueNamed(name)?.value as List<*>? ?: emptyList<AnnotationValue>()).map {
//            ((it as AnnotationValue).value as TypeMirror).asClassName()
//        }

    private fun getValueNamed(name: String): AnnotationValue? =
        mirror.elementValues.entries.find { it.key.simpleName.contentEquals(name) }?.value

    override fun toAnnotationSpec(): AnnotationSpec = AnnotationSpec.get(mirror)

//    private fun TypeMirror.asClassName(): ClassName = ClassName.get(MoreTypes.asTypeElement(this))
}