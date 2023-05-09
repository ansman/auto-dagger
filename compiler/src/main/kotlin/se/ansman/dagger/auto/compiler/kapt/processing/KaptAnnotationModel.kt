package se.ansman.dagger.auto.compiler.kapt.processing

import com.google.auto.common.MoreTypes
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import se.ansman.dagger.auto.compiler.processing.AnnotationModel
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

    override fun <T : Any> getValue(type: Class<T>, name: String): T? =
        getValueNamed(name)
            ?.accept(KaptAnnotationValueVisitor, KaptAnnotationValueVisitor.Context(resolver, type))
            ?.let(type::cast)

    private fun getValueNamed(name: String): AnnotationValue? =
        mirror.elementValues.entries.find { it.key.simpleName.contentEquals(name) }?.value

    override fun toAnnotationSpec(): AnnotationSpec = AnnotationSpec.get(mirror)
}

private object KaptAnnotationValueVisitor : AnnotationValueVisitor<Any, KaptAnnotationValueVisitor.Context> {
    override fun visit(av: AnnotationValue, p: Context): Any = av.accept(this, p)
    override fun visitBoolean(b: Boolean, p: Context): Any = b
    override fun visitByte(b: Byte, p: Context): Any = b
    override fun visitChar(c: Char, p: Context): Any = c
    override fun visitDouble(d: Double, p: Context): Any = d
    override fun visitFloat(f: Float, p: Context): Any = f
    override fun visitInt(i: Int, p: Context): Any = i
    override fun visitLong(i: Long, p: Context): Any = i
    override fun visitShort(s: Short, p: Context): Any = s
    override fun visitString(s: String, p: Context): Any = s
    override fun visitType(t: TypeMirror, p: Context): Any =
        KaptClassDeclaration(MoreTypes.asTypeElement(t), p.resolver)

    override fun visitEnumConstant(c: VariableElement, p: Context): Any =
        requireNotNull(p.type.enumConstants.find { (it as Enum<*>).name == c.simpleName.toString() }) {
            "Unknown enum constant ${c.simpleName} on ${p.type}"
        }

    override fun visitAnnotation(a: AnnotationMirror, p: Context): Any = KaptAnnotationModel(a, p.resolver)
    override fun visitArray(vals: List<AnnotationValue>, p: Context): Any = vals.map { it.accept(this, p) }

    override fun visitUnknown(av: AnnotationValue, p: Context): Any {
        throw UnsupportedOperationException("Unsupported annotation value $av")
    }

    class Context(
        val resolver: KaptResolver,
        val type: Class<*>,
    )
}
