package se.ansman.deager.kapt

import com.squareup.javapoet.ClassName
import se.ansman.deager.TypeLookup
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements

class KaptTypeLookup(private val elementUtils: Elements) : TypeLookup<TypeMirror> {
    private val cache = mutableMapOf<ClassName, TypeMirror>()

    override fun getTypeForName(className: ClassName): TypeMirror =
        cache.getOrPut(className) {
            requireNotNull(elementUtils.getTypeElement(className.canonicalName())?.asType()) {
                "Could not find element with name $className"
            }
        }
}