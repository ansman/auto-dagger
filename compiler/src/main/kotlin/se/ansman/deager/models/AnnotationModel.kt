package se.ansman.deager.models

import kotlin.reflect.KClass

interface AnnotationModel<out AnnotationSpec> {
    val declaredAnnotations: List<AnnotationModel<AnnotationSpec>>
    fun isOfType(type: KClass<*>): Boolean
    fun <T : Any> getValue(name: String): T?
    fun toAnnotationSpec(): AnnotationSpec
}