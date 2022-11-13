package se.ansman.deager.models

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName

interface AnnotationModel {
    val declaredAnnotations: List<AnnotationModel>
    fun isOfType(className: ClassName): Boolean
    fun <T : Any> getValue(name: String): T?
    fun toAnnotationSpec(): AnnotationSpec
}