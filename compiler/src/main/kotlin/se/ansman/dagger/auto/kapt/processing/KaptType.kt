package se.ansman.dagger.auto.kapt.processing

import com.google.auto.common.MoreTypes
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.processing.Type
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass

data class KaptType(
    val mirror: TypeMirror,
    val resolver: KaptResolver,
) : Type<Element, TypeName, ClassName, AnnotationSpec> {
    override val declaration: KaptClassDeclaration by lazy(LazyThreadSafetyMode.NONE) {
        KaptClassDeclaration(MoreTypes.asTypeElement(mirror), resolver)
    }

    override fun toTypeName(): TypeName = TypeName.get(mirror)
    override fun isAssignableTo(type: ClassName): Boolean = isAssignableTo(type.canonicalName())
    override fun isAssignableTo(type: KClass<*>): Boolean = isAssignableTo(type.qualifiedName!!)
    private fun isAssignableTo(type: String): Boolean =
        resolver.environment.environment.typeUtils.isAssignable(mirror, resolver.typeLookup[type].asType().mirror)
}