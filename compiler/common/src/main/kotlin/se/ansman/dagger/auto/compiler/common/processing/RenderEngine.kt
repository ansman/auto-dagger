package se.ansman.dagger.auto.compiler.common.processing

import kotlin.reflect.KClass

interface RenderEngine<TypeName, ClassName : TypeName, AnnotationSpec> {
    fun className(packageName: String, simpleName: String): ClassName
    fun className(qualifiedName: String): ClassName
    fun className(type: KClass<*>): ClassName

    fun typeArguments(typeName: TypeName): List<TypeName>

    fun simpleName(typeName: TypeName): String
    fun simpleNames(className: ClassName): List<String>
    fun packageName(className: ClassName): String
    fun topLevelClassName(className: ClassName): ClassName

    fun rawType(typeName: TypeName): ClassName

    fun asWildcard(typeName: TypeName): TypeName
}

fun <ClassName> RenderEngine<*, ClassName, *>.rootPeerClass(className: ClassName, name: String): ClassName =
    className(packageName(className), name)
