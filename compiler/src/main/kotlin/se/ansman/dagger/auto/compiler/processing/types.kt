package se.ansman.dagger.auto.compiler.processing

import javax.inject.Qualifier
import kotlin.reflect.KClass

interface Node<N, TypeName, ClassName : TypeName, AnnotationSpec> {
    val node: N
    val annotations: List<AnnotationModel<ClassName, AnnotationSpec>>
    val isPublic: Boolean
    val enclosingType: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>?
}

val Node<*, *, *, *>.isFullyPublic: Boolean
    get() = isPublic && enclosingType?.isPublic != false

interface Type<N, TypeName, ClassName : TypeName, AnnotationSpec> {
    val declaration: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>?
    fun toTypeName(): TypeName
    fun isAssignableTo(type: Type<N, TypeName, ClassName, AnnotationSpec>): Boolean
    fun isAssignableTo(type: KClass<*>): Boolean
    fun isAssignableTo(type: ClassName): Boolean
}

interface ExecutableNode<N, TypeName, ClassName : TypeName, AnnotationSpec> : Node<N, TypeName, ClassName, AnnotationSpec> {
    val name: String
    val receiver: Type<N, TypeName, ClassName, AnnotationSpec>?
    val returnType: Type<N, TypeName, ClassName, AnnotationSpec>
    val arguments: Sequence<Type<N, TypeName, ClassName, AnnotationSpec>>
}

interface Property<N, TypeName, ClassName : TypeName, AnnotationSpec> : ExecutableNode<N, TypeName, ClassName, AnnotationSpec> {
    override val arguments: Sequence<Type<N, TypeName, ClassName, AnnotationSpec>>
        get() = emptySequence()
}

interface Function<N, TypeName, ClassName : TypeName, AnnotationSpec> : ExecutableNode<N, TypeName, ClassName, AnnotationSpec>

interface ClassDeclaration<N, TypeName, ClassName : TypeName, AnnotationSpec> : Node<N, TypeName, ClassName, AnnotationSpec> {
    val className: ClassName
    val supertypes: List<Type<N, TypeName, ClassName, AnnotationSpec>>
    val isCompanionObject: Boolean
    val isGeneric: Boolean
    val superclass: Type<N, TypeName, ClassName, AnnotationSpec>?
    override val enclosingType: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>?
    fun asType(): Type<N, TypeName, ClassName, AnnotationSpec>
}

interface AnnotationModel<ClassName, AnnotationSpec> {
    val qualifiedName: String
    val declaredAnnotations: List<AnnotationModel<ClassName, AnnotationSpec>>
    fun isOfType(type: KClass<out Annotation>): Boolean
    fun isOfType(type: String): Boolean
    fun <T : Any> getValue(name: String): T?
    fun toAnnotationSpec(): AnnotationSpec
}

fun AnnotationModel<*, *>.isAnnotatedWith(kClass: KClass<out Annotation>): Boolean =
    declaredAnnotations.any { it.isOfType(kClass) }

fun <ClassName, AnnotationSpec> Node<*, *, ClassName, AnnotationSpec>.getAnnotation(
    annotation: KClass<out Annotation>
): AnnotationModel<ClassName, AnnotationSpec>? = annotations.find { it.isOfType(annotation) }

fun Node<*, *, *, *>.isAnnotatedWith(annotation: KClass<out Annotation>): Boolean = getAnnotation(annotation) != null

fun <AnnotationSpec> Node<*, *, *, AnnotationSpec>.getQualifiers(): Set<AnnotationSpec> =
    annotations.filterQualifiers().toSet()

fun <AnnotationSpec> Iterable<AnnotationModel<*, AnnotationSpec>>.filterQualifiers(): Sequence<AnnotationSpec> =
    asSequence()
        .filter { it.isAnnotatedWith(Qualifier::class) }
        .map { it.toAnnotationSpec() }