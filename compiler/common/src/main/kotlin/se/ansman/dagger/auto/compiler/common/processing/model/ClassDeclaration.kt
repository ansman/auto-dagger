package se.ansman.dagger.auto.compiler.common.processing.model

import kotlin.reflect.KClass

interface ClassDeclaration<N, TypeName, ClassName : TypeName> : Declaration<N, TypeName, ClassName> {
    val kind: Kind
    val className: ClassName
    val superTypes: List<Type<N, TypeName, ClassName>>
    val declarations: List<Declaration<N, TypeName, ClassName>>
    val isSealedClass: Boolean
    val superclass: Type<N, TypeName, ClassName>?
    override val enclosingDeclaration: ClassDeclaration<N, TypeName, ClassName>?
    fun asType(): Type<N, TypeName, ClassName>
    fun isType(qualifiedName: String): Boolean
    fun isType(className: ClassName): Boolean

    enum class Kind {
        Class,
        Interface,
        EnumClass,
        EnumEntry,
        AnnotationClass,
        Object,
        CompanionObject
    }
}

fun ClassDeclaration<*, *, *>.isType(kClass: KClass<*>): Boolean =
    isType(kClass.java.canonicalName)

inline fun <reified T> ClassDeclaration<*, *, *>.isType() = isType(T::class)