package se.ansman.dagger.auto.compiler.common.processing.model

import kotlin.reflect.KClass

interface Type<N, TypeName, ClassName : TypeName> {
    val declaration: ClassDeclaration<N, TypeName, ClassName>?
    val isGeneric: Boolean
    fun toTypeName(): TypeName
    fun isAssignableTo(type: Type<N, TypeName, ClassName>): Boolean
    fun isAssignableTo(type: ClassName): Boolean
    fun isAssignableTo(type: String): Boolean
}

fun Type<*, *, *>.isAssignableTo(type: KClass<*>): Boolean = isAssignableTo(type.qualifiedName!!)