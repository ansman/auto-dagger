package se.ansman.deager.kapt

import javax.lang.model.element.Element

inline fun <reified A : Annotation> Element.hasAnnotation(): Boolean = getAnnotation(A::class.java) != null