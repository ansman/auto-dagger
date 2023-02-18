package se.ansman.dagger.auto.kapt

import javax.lang.model.element.Element

inline fun <reified A : Annotation> Element.hasAnnotation(): Boolean = getAnnotation(A::class.java) != null