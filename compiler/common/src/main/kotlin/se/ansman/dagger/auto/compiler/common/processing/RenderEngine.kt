package se.ansman.dagger.auto.compiler.common.processing

import se.ansman.dagger.auto.compiler.common.processing.model.AnnotationNode
import kotlin.reflect.KClass

interface RenderEngine<N, TypeName, ClassName : TypeName, AnnotationSpec> {
    fun className(packageName: String, simpleName: String): ClassName
    fun className(qualifiedName: String): ClassName
    fun className(type: KClass<*>): ClassName

    fun qualifiedName(className: ClassName): String
    fun simpleName(typeName: TypeName): String
    fun simpleNames(className: ClassName): List<String>
    fun packageName(className: ClassName): String
    fun topLevelClassName(className: ClassName): ClassName


    fun rawType(typeName: TypeName): ClassName

    fun asWildcard(typeName: TypeName): TypeName

    fun AnnotationNode<N, TypeName, ClassName>.toAnnotationSpec(): AnnotationSpec
    fun Sequence<AnnotationNode<N, TypeName, ClassName>>.toAnnotationSpecs(): List<AnnotationSpec> =
        map { it.toAnnotationSpec() }.toList()
}

fun <ClassName> RenderEngine<*, *, ClassName, *>.rootPeerClass(className: ClassName, name: String): ClassName =
    className(packageName(className), name)
