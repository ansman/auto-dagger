package se.ansman.dagger.auto.compiler.common.processing.model

import kotlin.reflect.KClass

interface Declaration<N, TypeName, ClassName : TypeName> : Node<N, TypeName, ClassName> {
    val name: String
    val annotations: List<AnnotationNode<N, TypeName, ClassName>>
    val isPublic: Boolean
    val isPrivate: Boolean
    val isGeneric: Boolean
    val isAbstract: Boolean
    val enclosingDeclaration: Declaration<N, TypeName, ClassName>?
}

val <E, TypeName, ClassName : TypeName> Declaration<E, TypeName, ClassName>.enclosingType: ClassDeclaration<E, TypeName, ClassName>?
    get() {
        var parent = enclosingDeclaration
        while (parent != null) {
            if (parent is ClassDeclaration<E, TypeName, ClassName>) {
                return parent
            }
            parent = parent.enclosingDeclaration
        }
        return null
    }

@Suppress("RecursivePropertyAccessor")
val Declaration<*, *, *>.isFullyPublic: Boolean
    get() = isPublic && enclosingDeclaration?.isFullyPublic != false

@Suppress("RecursivePropertyAccessor")
val Declaration<*, *, *>.isFullyPrivate: Boolean
    get() = isPrivate || enclosingDeclaration?.isFullyPrivate == true

fun <N, TypeName, ClassName : TypeName> Declaration<N, TypeName, ClassName>.getAnnotation(
    annotation: ClassName
) = annotations.find { it.declaration.isType(annotation) }

fun <N, TypeName, ClassName : TypeName> Declaration<N, TypeName, ClassName>.getAnnotation(
    annotation: String
) = annotations.find { it.declaration.isType(annotation) }

fun <N, TypeName, ClassName : TypeName> Declaration<N, TypeName, ClassName>.getAnnotation(
    annotation: KClass<out Annotation>
) = annotations.find { it.declaration.isType(annotation) }

fun <N, TypeName, ClassName : TypeName> Declaration<N, TypeName, ClassName>.isAnnotatedWith(
    annotation: ClassName
): Boolean = getAnnotation(annotation) != null

fun <N, TypeName, ClassName : TypeName> Declaration<N, TypeName, ClassName>.isAnnotatedWith(annotation: String): Boolean =
    getAnnotation(annotation) != null

fun <N, TypeName, ClassName : TypeName> Declaration<N, TypeName, ClassName>.isAnnotatedWith(annotation: KClass<out Annotation>): Boolean =
    getAnnotation(annotation) != null

fun <N, TypeName, ClassName : TypeName> Declaration<N, TypeName, ClassName>.getQualifiers() =
    annotations.filterQualifiers()