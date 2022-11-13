package se.ansman.deager.kapt

import com.google.auto.common.MoreTypes
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import se.ansman.deager.models.AnnotationModel
import javax.lang.model.element.AnnotationMirror

class KaptAnnotationModel(private val mirror: AnnotationMirror) : AnnotationModel {
    private val className by lazy {
        mirror.annotationType.asElement().asType()
            .let(MoreTypes::asTypeElement)
            .let(ClassName::get)
    }
    override val declaredAnnotations: List<KaptAnnotationModel> by lazy {
        mirror.annotationType.asElement().annotationMirrors.map(::KaptAnnotationModel)
    }

    override fun isOfType(className: ClassName): Boolean =
        mirror.annotationType.asElement().simpleName.contentEquals(className.simpleName()) &&
                className == this.className

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getValue(name: String): T? =
        mirror.elementValues.entries.find { it.key.simpleName.contentEquals(name) }?.value?.value as T?

    override fun toAnnotationSpec(): AnnotationSpec = AnnotationSpec.get(mirror)
}