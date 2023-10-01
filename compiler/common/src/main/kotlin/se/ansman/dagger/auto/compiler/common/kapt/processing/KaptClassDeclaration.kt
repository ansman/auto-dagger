package se.ansman.dagger.auto.compiler.common.kapt.processing

import com.google.auto.common.MoreElements
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import kotlinx.metadata.ClassKind
import kotlinx.metadata.Modality
import kotlinx.metadata.Visibility
import kotlinx.metadata.kind
import kotlinx.metadata.modality
import kotlinx.metadata.visibility
import se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind

data class KaptClassDeclaration(
    override val node: TypeElement,
    override val resolver: KaptResolver,
) : KaptNode(), ClassDeclaration<Element, TypeName, ClassName, AnnotationSpec> {
    private val kmClass by lazy { resolver.kmClassLookup[node.qualifiedName.toString()] }

    override val kind: ClassDeclaration.Kind
        get() = if (kmClass != null) {
            when (kmClass!!.kind) {
                ClassKind.CLASS -> ClassDeclaration.Kind.Class
                ClassKind.INTERFACE -> ClassDeclaration.Kind.Interface
                ClassKind.ENUM_CLASS -> ClassDeclaration.Kind.EnumClass
                ClassKind.ENUM_ENTRY -> ClassDeclaration.Kind.EnumEntry
                ClassKind.ANNOTATION_CLASS -> ClassDeclaration.Kind.AnnotationClass
                ClassKind.OBJECT -> ClassDeclaration.Kind.Object
                ClassKind.COMPANION_OBJECT -> ClassDeclaration.Kind.CompanionObject
            }
        } else when (node.kind) {
            ElementKind.ENUM -> ClassDeclaration.Kind.EnumClass
            ElementKind.CLASS -> ClassDeclaration.Kind.Class
            ElementKind.ANNOTATION_TYPE -> ClassDeclaration.Kind.AnnotationClass
            ElementKind.INTERFACE -> ClassDeclaration.Kind.Interface
            ElementKind.ENUM_CONSTANT -> ClassDeclaration.Kind.EnumEntry
            else -> {
                resolver.environment.logger.error("Unsupported element kind: ${node.kind}", node)
                ClassDeclaration.Kind.Class
            }
        }

    override val className: ClassName by lazy(LazyThreadSafetyMode.NONE) { ClassName.get(node) }

    override val supertypes: List<KaptType> by lazy(LazyThreadSafetyMode.NONE) {
        buildList {
            superclass?.let { add(it) }
            node.interfaces.mapTo(this) { KaptType(it, resolver) }
        }
    }

    @Suppress("UnstableApiUsage")
    override val declaredNodes: List<KaptFunction> by lazy(LazyThreadSafetyMode.NONE) {
        node.enclosedElements
            .filter { it.kind == ElementKind.METHOD }
            .map(MoreElements::asExecutable)
            .map { KaptFunction(it, resolver) }
    }

    override val isGeneric: Boolean
        get() = node.typeParameters.isNotEmpty()

    override val isAbstract: Boolean
        get() = Modifier.ABSTRACT in node.modifiers

    override val isSealedClass: Boolean
        get() = kmClass?.modality == Modality.SEALED

    override val isPublic: Boolean
        get() = kmClass?.let { it.visibility == Visibility.PUBLIC } ?: (Modifier.PUBLIC in node.modifiers)

    override val isPrivate: Boolean
        get() = kmClass?.let { it.visibility == Visibility.PRIVATE } ?: false // Java classes cannot be private

    override val superclass: KaptType? by lazy(LazyThreadSafetyMode.NONE) {
        node.superclass
            .takeUnless { it.kind == TypeKind.NONE || it.isObject }
            ?.let { KaptType(it, resolver) }
    }

    override fun asType(): KaptType = KaptType(node.asType(), resolver)
}