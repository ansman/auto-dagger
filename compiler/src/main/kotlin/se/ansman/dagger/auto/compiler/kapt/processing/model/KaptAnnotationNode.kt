package se.ansman.dagger.auto.compiler.kapt.processing.model

import com.google.auto.common.MoreTypes
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.model.AnnotationNode
import se.ansman.dagger.auto.compiler.kapt.processing.KaptResolver
import java.lang.reflect.Type
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.AnnotationValueVisitor
import javax.lang.model.element.Element
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

class KaptAnnotationNode(
    val mirror: AnnotationMirror,
    override val resolver: KaptResolver,
) : KaptNode(), AnnotationNode<Element, TypeName, ClassName> {
    override val node: Element
        get() = throw UnsupportedOperationException("KaptAnnotationNode does not have a node")

    override val declaration: KaptClassDeclaration by lazy(LazyThreadSafetyMode.NONE) {
        mirror.annotationType.asElement().asType()
            .let(MoreTypes::asTypeElement)
            .let { KaptClassDeclaration(it, resolver) }
    }

    override fun <T : Any> getValue(type: Type, name: String): T? =
        @Suppress("UNCHECKED_CAST")
        getValueNamed(name)
            ?.accept(KaptAnnotationValueVisitor, KaptAnnotationValueVisitor.Context(resolver, type))
            ?.let { it as T }

    private fun getValueNamed(name: String): AnnotationValue? =
        mirror.elementValues.entries.find { it.key.simpleName.contentEquals(name) }?.value
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
        requireNotNull((p.type as Class<*>).enumConstants.find { (it as Enum<*>).name == c.simpleName.toString() }) {
            "Unknown enum constant ${c.simpleName} on ${p.type}"
        }

    override fun visitAnnotation(a: AnnotationMirror, p: Context): Any = KaptAnnotationNode(a, p.resolver)
    override fun visitArray(vals: List<AnnotationValue>, p: Context): Any = vals.map { it.accept(this, p) }

    override fun visitUnknown(av: AnnotationValue, p: Context): Any {
        throw UnsupportedOperationException("Unsupported annotation value $av")
    }

    class Context(
        val resolver: KaptResolver,
        val type: Type,
    )
}
