package se.ansman.dagger.auto.compiler.kapt.processing

import com.google.auto.common.MoreTypes
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.Type
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror

data class KaptType(
    val mirror: TypeMirror,
    val resolver: KaptResolver,
) : Type<Element, TypeName, ClassName, AnnotationSpec> {
    override val declaration: KaptClassDeclaration? by lazy(LazyThreadSafetyMode.NONE) {
        if (mirror.kind.isPrimitive) {
            null
        } else {
            KaptClassDeclaration(MoreTypes.asTypeElement(mirror), resolver)
        }
    }

    override val isGeneric: Boolean
        get() = MoreTypes.asTypeElement(mirror).typeParameters.isNotEmpty()

    override fun toTypeName(): TypeName = TypeName.get(mirror)
    override fun isAssignableTo(type: Type<Element, TypeName, ClassName, AnnotationSpec>): Boolean =
        isAssignableTo((type as KaptType).mirror)
    override fun isAssignableTo(type: ClassName): Boolean = isAssignableTo(type.canonicalName())
    override fun isAssignableTo(type: String): Boolean = isAssignableTo(resolver.typeLookup[type].asType().mirror)
    private fun isAssignableTo(type: TypeMirror): Boolean =
        resolver.environment.environment.typeUtils.isAssignable(mirror, type)
}