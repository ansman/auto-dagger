package se.ansman.dagger.auto.kapt.processing

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.processing.ClassDeclaration
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind

data class KaptClassDeclaration(
    override val node: TypeElement,
    override val processing: KaptProcessing,
) : KaptNode(), ClassDeclaration<Element, TypeName, ClassName, AnnotationSpec> {
    override val supertypes: Sequence<KaptType>
        get() = sequence {
            superclass?.let { yield(it) }
            for (typeMirror in node.interfaces) {
                yield(KaptType(typeMirror, processing))
            }
        }

    override val isCompanionObject: Boolean
        // We cannot determine this in Kapt
        get() = false

    override val superclass: KaptType? by lazy(LazyThreadSafetyMode.NONE) {
        node.superclass
            .takeUnless { it.kind == TypeKind.NONE }
            ?.let { KaptType(it, processing) }
    }

    override fun toClassName(): ClassName = ClassName.get(node)
    override fun asType(): KaptType = KaptType(node.asType(), processing)
}