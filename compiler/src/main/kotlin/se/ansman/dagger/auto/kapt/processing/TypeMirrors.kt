package se.ansman.dagger.auto.kapt.processing

import com.google.auto.common.MoreTypes
import javax.lang.model.type.TypeMirror

val TypeMirror.isObject: Boolean
    get() = MoreTypes.isTypeOf(Object::class.java, this)