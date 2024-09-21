package se.ansman.dagger.auto.compiler.utils

import dagger.Reusable
import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.compiler.Errors
import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.processing.*
import javax.inject.Scope
import javax.inject.Singleton

fun <N, TypeName, ClassName : TypeName, AnnotationSpec> Processor<N, TypeName, ClassName, AnnotationSpec>.getTargetComponent(
    declaration: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>,
    resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>,
    annotation: AnnotationModel<ClassName, AnnotationSpec>,
): ClassName? =
    annotation.getValue<ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>>("inComponent")
        ?.also { validateComponent(declaration, resolver, it) }
        ?.className
        ?: guessComponent(declaration)

fun <N, ClassName> Processor<N, *, ClassName, *>.guessComponent(
    declaration: ClassDeclaration<N, *, ClassName, *>
): ClassName? {
    val scope = declaration.annotations.find { it.isAnnotatedWith(Scope::class) }
        ?: return environment.className(SingletonComponent::class)
    return guessComponent(scope) ?: declaration.run {
        logger?.error(Errors.nonStandardScope(scope.qualifiedName), this)
        null
    }
}

fun <N, TypeName, ClassName : TypeName, AnnotationSpec> Processor<N, TypeName, ClassName, AnnotationSpec>.validateComponent(
    declaration: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>,
    resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>,
    component: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>,
) {
    if (!validateComponent(component, declaration)) {
        return
    }
    val scope = declaration.annotations.find { it.isAnnotatedWith(Scope::class) }
        ?: return
    val inferredComponent = guessComponent(scope) ?: return
    if (resolver.isComponentChildComponent(component, inferredComponent)) {
        logger?.error(
            message = Errors.parentComponent(
                inComponent = resolver.environment.simpleName(component.className),
                inferredComponent = resolver.environment.simpleName(inferredComponent)
            ),
            node = declaration
        )
    }
}

fun <ClassName> Processor<*, *, ClassName, *>.guessComponent(annotation: AnnotationModel<*, *>): ClassName? =
    when (annotation.qualifiedName) {
        Singleton::class.java.canonicalName,
        Reusable::class.java.canonicalName ->
            environment.className(SingletonComponent::class)

        "dagger.hilt.android.scopes.ActivityRetainedScoped" ->
            environment.className("dagger.hilt.android.components", "ActivityRetainedComponent")

        "dagger.hilt.android.scopes.ActivityScoped" ->
            environment.className("dagger.hilt.android.components", "ActivityComponent")

        "dagger.hilt.android.scopes.FragmentScoped" ->
            environment.className("dagger.hilt.android.components", "FragmentComponent")

        "dagger.hilt.android.scopes.ServiceScoped" ->
            environment.className("dagger.hilt.android.components", "ServiceComponent")

        "dagger.hilt.android.scopes.ViewScoped" ->
            environment.className("dagger.hilt.android.components", "ViewComponent")

        "dagger.hilt.android.scopes.ViewModelScoped" ->
            environment.className("dagger.hilt.android.components", "ViewModelComponent")

        "dagger.hilt.android.scopes.ViewWithFragmentScoped" ->
            environment.className("dagger.hilt.android.components", "ViewWithFragmentComponent")

        else -> null
    }

private fun <ClassName> AutoDaggerResolver<*, *, ClassName, *>.isComponentChildComponent(
    installInComponent: ClassDeclaration<*, *, ClassName, *>,
    inferredComponent: ClassName,
): Boolean {
    var c = inferredComponent
    do {
        c = lookupType(c)
            .getAnnotation(DefineComponent::class)
            ?.getValue<ClassDeclaration<*, *, ClassName, *>>("parent")
            ?.className
            ?: return false
    } while (c != installInComponent.className)
    return true
}

private fun <N> Processor<N, *, *, *>.validateComponent(
    declaration: ClassDeclaration<N, *, *, *>,
    node: Node<N, *, *, *>
): Boolean {
    val defineComponent = declaration.getAnnotation(DefineComponent::class)
    if (defineComponent == null) {
        logger?.error(Errors.invalidComponent(declaration.node.toString()), node)
        return false
    }
    return true
}