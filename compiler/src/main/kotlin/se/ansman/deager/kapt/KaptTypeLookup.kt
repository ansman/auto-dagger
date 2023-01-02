package se.ansman.deager.kapt

import se.ansman.deager.TypeLookup
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import kotlin.reflect.KClass

class KaptTypeLookup(private val elementUtils: Elements) : TypeLookup<TypeMirror> {
    private val cache = mutableMapOf<KClass<*>, TypeMirror>()

    override fun getTypeForClass(klass: KClass<*>): TypeMirror =
        cache.getOrPut(klass) {
            val name = requireNotNull(klass.qualifiedName) {
                "Cannot get type mirror for anonymous or local class $klass"
            }
            requireNotNull(elementUtils.getTypeElement(name)?.asType()) {
                "Could not find element with name $klass"
            }
        }
}