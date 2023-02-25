package se.ansman.dagger.auto.processing

import kotlin.reflect.KClass

interface RenderEngine<TypeName, ClassName : TypeName, AnnotationSpec> {
    fun className(packageName: String, simpleName: String): ClassName
    fun className(qualifiedName: String): ClassName
    fun className(type: KClass<*>): ClassName

    fun simpleName(className: ClassName): String
    fun simpleNames(className: ClassName): List<String>
    fun packageName(className: ClassName): String
    fun topLevelClassName(className: ClassName): ClassName

    fun rawType(typeName: TypeName): ClassName
}

fun <ClassName> RenderEngine<*, ClassName, *>.rootPeerClass(className: ClassName, name: String): ClassName =
    className(packageName(className), name)
