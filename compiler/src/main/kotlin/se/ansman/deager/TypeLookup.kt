package se.ansman.deager

import com.squareup.javapoet.ClassName

interface TypeLookup<T : Any> {
    fun getTypeForName(className: ClassName): T
}