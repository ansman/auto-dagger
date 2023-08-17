package se.ansman.dagger.auto.compiler.common.processing

import org.checkerframework.checker.units.qual.N
import se.ansman.dagger.auto.compiler.common.TypeLookup
import kotlin.reflect.KClass

interface AutoDaggerResolver<N, TypeName, ClassName : TypeName, AnnotationSpec> {
    val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, *>
    val typeLookup: TypeLookup<ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>>

    fun nodesAnnotatedWith(annotation: String): Sequence<Node<N, TypeName, ClassName, AnnotationSpec>>
    fun lookupType(className: ClassName): ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>
}

fun <N, TypeName, ClassName : TypeName, AnnotationSpec> AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>
        .lookupType(className: KClass<*>): ClassDeclaration<N, TypeName, ClassName, AnnotationSpec> =
    lookupType(environment.renderEngine.className(className))

fun <N, TypeName, ClassName : TypeName, AnnotationSpec> AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>
        .nodesAnnotatedWith(annotation: KClass<out Annotation>): Sequence<Node<N, TypeName, ClassName, AnnotationSpec>> =
    nodesAnnotatedWith(annotation.java.name)