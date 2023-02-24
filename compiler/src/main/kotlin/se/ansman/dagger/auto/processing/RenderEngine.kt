package se.ansman.dagger.auto.processing

import kotlin.reflect.KClass

interface RenderEngine<TypeName, ClassName : TypeName, AnnotationSpec> {
    fun className(packageName: String, simpleName: String): ClassName
    fun className(qualifiedName: String): ClassName

    val ClassName.simpleName: String
    val ClassName.simpleNames: List<String>
    val ClassName.packageName: String
    val ClassName.topLevelClassName: ClassName
    fun ClassName.peerClass(name: String): ClassName

    val TypeName.rawType: ClassName

    fun KClass<*>.toClassName(): ClassName
}