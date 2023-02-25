package se.ansman.dagger.auto.kapt.processing

import com.google.auto.common.MoreTypes
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import se.ansman.dagger.auto.processing.AnnotationModel
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.AnnotationValueVisitor
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass

class KaptAnnotationModel(
    private val mirror: AnnotationMirror,
    private val resolver: KaptResolver,
) : AnnotationModel<ClassName, AnnotationSpec> {
    override val qualifiedName: String
        get() = className.canonicalName()

    private val className by lazy {
        mirror.annotationType.asElement().asType()
            .let(MoreTypes::asTypeElement)
            .let(ClassName::get)
    }
    override val declaredAnnotations: List<KaptAnnotationModel> by lazy {
        mirror.annotationType.asElement().annotationMirrors.map { KaptAnnotationModel(it, resolver) }
    }

    override fun isOfType(type: KClass<out Annotation>): Boolean =
        mirror.annotationType.asElement().simpleName.contentEquals(type.simpleName) &&
                ClassName.get(type.java) == className

    override fun isOfType(type: String): Boolean =
        type.endsWith(mirror.annotationType.asElement().simpleName) && type == className.canonicalName()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getValue(name: String): T? =
        getValueNamed(name)?.accept(AnnotationValueVisitor, resolver) as T?

    private fun getValueNamed(name: String): AnnotationValue? =
        mirror.elementValues.entries.find { it.key.simpleName.contentEquals(name) }?.value

    override fun toAnnotationSpec(): AnnotationSpec = AnnotationSpec.get(mirror)
}

private object AnnotationValueVisitor : AnnotationValueVisitor<Any, KaptResolver> {
    override fun visit(av: AnnotationValue, p: KaptResolver): Any = av.accept(this, p)
    override fun visitBoolean(b: Boolean, p: KaptResolver): Any = b
    override fun visitByte(b: Byte, p: KaptResolver): Any = b
    override fun visitChar(c: Char, p: KaptResolver): Any = c
    override fun visitDouble(d: Double, p: KaptResolver): Any = d
    override fun visitFloat(f: Float, p: KaptResolver): Any = f
    override fun visitInt(i: Int, p: KaptResolver): Any = i
    override fun visitLong(i: Long, p: KaptResolver): Any = i
    override fun visitShort(s: Short, p: KaptResolver): Any = s
    override fun visitString(s: String, p: KaptResolver): Any = s
    override fun visitType(t: TypeMirror, p: KaptResolver): Any = KaptClassDeclaration(MoreTypes.asTypeElement(t), p)

    override fun visitEnumConstant(c: VariableElement?, p: KaptResolver): Any {
        throw UnsupportedOperationException("Enum type annotation values aren't supported")
    }

    override fun visitAnnotation(a: AnnotationMirror, p: KaptResolver): Any = KaptAnnotationModel(a, p)
    override fun visitArray(vals: List<AnnotationValue>, p: KaptResolver): Any = vals.map { it.accept(this, p) }

    override fun visitUnknown(av: AnnotationValue, p: KaptResolver): Any {
        throw UnsupportedOperationException("Unsupported annotation value $av")
    }
}