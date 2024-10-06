package se.ansman.dagger.auto.compiler.common.processing.model

import java.lang.reflect.Type
import javax.inject.Qualifier
import kotlin.reflect.KClass
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

interface AnnotationNode<N, TypeName, ClassName : TypeName> : Node<N, TypeName, ClassName> {
    val declaration: ClassDeclaration<N, TypeName, ClassName>
    fun <T : Any> getValue(type: Type, name: String): T?
}

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Any> AnnotationNode<*, *, *>.getValue(name: String): T? =
    getValue(typeOf<T>().javaType, name)

fun <N, TypeName, ClassName : TypeName> AnnotationNode<N, TypeName, ClassName>.getClassValue(
    name: String
): ClassDeclaration<N, TypeName, ClassName>? = getValue(name)

fun <N, TypeName, ClassName : TypeName> AnnotationNode<N, TypeName, ClassName>.getClassArrayValue(
    name: String
): List<ClassDeclaration<N, TypeName, ClassName>>? = getValue(name)

fun AnnotationNode<*, *, *>.isAnnotatedWith(kClass: KClass<out Annotation>): Boolean =
    declaration.annotations.any { it.declaration.isType(kClass) }

fun <N, TypeName, ClassName : TypeName> Iterable<AnnotationNode<N, TypeName, ClassName>>.filterQualifiers() =
    asSequence()
        .filter { it.isAnnotatedWith(Qualifier::class) }