package se.ansman.dagger.auto.compiler.utils

import dagger.Reusable
import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.compiler.Errors
import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.processing.AnnotationModel
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration
import se.ansman.dagger.auto.compiler.common.processing.Node
import se.ansman.dagger.auto.compiler.common.processing.error
import se.ansman.dagger.auto.compiler.common.processing.getAnnotation
import se.ansman.dagger.auto.compiler.common.processing.getValue
import se.ansman.dagger.auto.compiler.common.processing.isAnnotatedWith
import javax.inject.Scope
import javax.inject.Singleton

context(Processor<N, TypeName, ClassName, AnnotationSpec>)
fun <N, TypeName, ClassName: TypeName, AnnotationSpec> ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.getTargetComponent(
    resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>,
    annotation: AnnotationModel<ClassName, AnnotationSpec>,
): ClassName? =
    annotation.getValue<ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>>("inComponent")
        ?.also { validateComponent(resolver, it) }
        ?.className
        ?: guessComponent()

context(Processor<N, *, ClassName, *>)
fun <N, ClassName> ClassDeclaration<N, *, ClassName, *>.guessComponent(): ClassName? {
    val scope = annotations.find { it.isAnnotatedWith(Scope::class) }
        ?: return environment.className(SingletonComponent::class)
    return scope.guessComponent() ?: run {
        logger?.error(Errors.nonStandardScope(scope.qualifiedName), this)
        null
    }
}

context(Processor<N, TypeName, ClassName, AnnotationSpec>)
fun <N, TypeName, ClassName: TypeName, AnnotationSpec> ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.validateComponent(
    resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>,
    component: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>,
) {
    if (!component.validateComponent(this)) {
        return
    }
    val scope = annotations.find { it.isAnnotatedWith(Scope::class) }
        ?: return
    val inferredComponent = scope.guessComponent() ?: return
    if (isComponentChildComponent(resolver, component, inferredComponent)) {
        logger?.error(
            message = Errors.parentComponent(
                inComponent = resolver.environment.simpleName(component.className),
                inferredComponent = resolver.environment.simpleName(inferredComponent)
            ),
            node = this
        )
    }
}

context(Processor<*, *, ClassName, *>)
fun <ClassName> AnnotationModel<*, *>.guessComponent(): ClassName? =
    when (qualifiedName) {
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

context(Processor<*, *, ClassName, *>)
private fun <ClassName> isComponentChildComponent(
    resolver: AutoDaggerResolver<*, *, ClassName, *>,
    installInComponent: ClassDeclaration<*, *, ClassName, *>,
    inferredComponent: ClassName,
): Boolean {
    var c = inferredComponent
    do {
        c = resolver.lookupType(c)
            .getAnnotation(DefineComponent::class)
            ?.getValue<ClassDeclaration<*, *, ClassName, *>>("parent")
            ?.className
            ?: return false
    } while (c != installInComponent.className)
    return true
}

context(Processor<N, *, *, *>)
private fun <N> ClassDeclaration<N, *, *, *>.validateComponent(node: Node<N, *, *, *>): Boolean {
    val defineComponent = getAnnotation(DefineComponent::class)
    if (defineComponent == null) {
        logger?.error(Errors.invalidComponent(this.node.toString()), node)
        return false
    }
    return true
}