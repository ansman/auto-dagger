package se.ansman.dagger.auto.processors

import dagger.MapKey
import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.AutoBind
import se.ansman.dagger.auto.AutoBindIntoMap
import se.ansman.dagger.auto.AutoBindIntoSet
import se.ansman.dagger.auto.Errors
import se.ansman.dagger.auto.models.AutoBindObject
import se.ansman.dagger.auto.models.AutoBindType
import se.ansman.dagger.auto.processing.AnnotationModel
import se.ansman.dagger.auto.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.processing.AutoDaggerResolver
import se.ansman.dagger.auto.processing.ClassDeclaration
import se.ansman.dagger.auto.processing.getAnnotation
import se.ansman.dagger.auto.processing.getQualifiers
import se.ansman.dagger.auto.processing.isAnnotatedWith
import se.ansman.dagger.auto.processing.isFullyPublic
import se.ansman.dagger.auto.processing.logError
import se.ansman.dagger.auto.renderers.HiltModuleBuilder
import se.ansman.dagger.auto.renderers.Renderer
import javax.inject.Scope
import javax.inject.Singleton
import kotlin.reflect.KClass

class AutoBindProcessor<N, TypeName : Any, ClassName : TypeName, AnnotationSpec, F>(
    private val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: Renderer<AutoBindObject<N, TypeName, ClassName, AnnotationSpec>, F>,
) : Processor<N, TypeName, ClassName, AnnotationSpec> {
    override val annotations: Set<KClass<out Annotation>>
        get() = setOf(
            AutoBind::class,
            AutoBindIntoSet::class,
            AutoBindIntoMap::class,
        )

    override fun process(resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>) {
        environment.logInfo("AutoBind processing started")
        annotations.asSequence()
            .onEach { environment.logInfo("Looking for annotation $it") }
            .flatMap { resolver.nodesAnnotatedWith(it) }
            .distinct()
            .map { it as ClassDeclaration }
            .forEach { node ->
                try {
                    node.process(resolver)
                } catch (e: AbortProcessingError) {
                    return@forEach
                }
            }
    }

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.process(
        resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>
    ) {
        environment.logInfo("Processing $className")
        if (isGeneric) {
            environment.logError(Errors.AutoBind.genericType, this)
            return
        }

        val objects: MutableMap<ModuleKey<ClassName>, AutoBindObject<N, TypeName, ClassName, AnnotationSpec>> =
            mutableMapOf()

        getAnnotation(AutoBind::class)?.let { annotation ->
            process(resolver, HiltModuleBuilder.ProviderMode.Single, annotation, objects)
        }

        getAnnotation(AutoBindIntoSet::class)?.let { annotation ->
            process(resolver, HiltModuleBuilder.ProviderMode.IntoSet, annotation, objects)
        }

        getAnnotation(AutoBindIntoMap::class)?.let { annotation ->
            val bindingKeys = annotations
                .filter { it.isAnnotatedWith(MapKey::class) }

            when (bindingKeys.size) {
                0 ->
                    environment.logError(Errors.AutoBind.missingBindingKey, this)

                1 ->
                    process(
                        resolver,
                        HiltModuleBuilder.ProviderMode.IntoMap(bindingKeys.single().toAnnotationSpec()),
                        annotation,
                        objects
                    )

                else ->
                    environment.logError(Errors.AutoBind.multipleBindingKeys, this)
            }
        }
        environment.logInfo("Found ${objects.values.size} for $className")
        objects.values
            .asSequence()
            .map(renderer::render)
            .forEach(environment::write)
    }

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.process(
        resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>,
        mode: HiltModuleBuilder.ProviderMode<AnnotationSpec>,
        annotation: AnnotationModel<ClassName, AnnotationSpec>,
        output: MutableMap<ModuleKey<ClassName>, AutoBindObject<N, TypeName, ClassName, AnnotationSpec>>,
    ) {
        environment.logInfo("Processing annotation ${annotation.qualifiedName} for $className")

        val component = getTargetComponent(resolver, annotation)
        val key = ModuleKey(targetType = className, targetComponent = component)
        val types = getBoundTypes(annotation).map { AutoBindType(it, mode) }
        output[key] = output[key]
            ?.withTypesAdded(types)
            ?: AutoBindObject(
                sourceType = className,
                targetComponent = component,
                isPublic = isFullyPublic,
                boundTypes = types,
                qualifiers = getQualifiers(),
                originatingElement = node,
                originatingTopLevelClassName = environment.renderEngine.topLevelClassName(className)
            )
    }

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.getTargetComponent(
        resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>,
        annotation: AnnotationModel<ClassName, AnnotationSpec>,
    ): ClassName =
        annotation.getValue<ClassDeclaration<*, *, ClassName, *>>("inComponent")
            ?.className
            ?.also { validateComponent(resolver, it) }
            ?: guessComponent()

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.guessComponent(): ClassName {
        val scope = annotations.find { it.isAnnotatedWith(Scope::class) }
            ?: return environment.renderEngine.className(SingletonComponent::class)
        return scopeToComponent(scope) ?: run {
            environment.logError(Errors.AutoBind.nonStandardScope(scope.qualifiedName), this)
            throw AbortProcessingError()
        }
    }

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.validateComponent(
        resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>,
        component: ClassName
    ) {
        if (!resolver.lookupType(component).isAnnotatedWith(DefineComponent::class)) {
            environment.logError(Errors.AutoBind.invalidComponent(component.toString()), this)
        }
    }

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.getBoundTypes(
        annotation: AnnotationModel<ClassName, AnnotationSpec>,
    ): List<TypeName> {
        val asTypes = annotation.getValue<List<ClassDeclaration<*, *, ClassName, *>>>("asTypes")
            ?.takeUnless { it.isEmpty() }
            ?.mapTo(mutableSetOf()) { it.className }
            ?: return supertypes.map { it.toTypeName() }.toList().also {
                if (it.isEmpty()) {
                    environment.logError(Errors.AutoBind.noSuperTypes, this)
                }
            }

        val supertypes = supertypes.associateByTo(mutableMapOf()) {
            environment.renderEngine.rawType(it.toTypeName())
        }
        supertypes.keys.retainAll(asTypes)
        val missingTypes = asTypes - supertypes.keys
        for (missingType in missingTypes) {
            val error = if (asType().isAssignableTo(missingType)) {
                Errors.AutoBind::missingDirectSuperType
            } else {
                Errors.AutoBind::missingBoundType
            }
            environment.logError(error(missingType.toString()), this)
        }
        if (missingTypes.isNotEmpty()) {
            throw AbortProcessingError()
        }
        return supertypes.values.map { it.toTypeName() }
    }

    private fun scopeToComponent(scope: AnnotationModel<*, *>): ClassName? =
        when (scope.qualifiedName) {
            Singleton::class.java.name ->
                environment.renderEngine.className(SingletonComponent::class)

            "dagger.hilt.android.scopes.ActivityRetainedScoped" ->
                environment.renderEngine.className("dagger.hilt.android.components", "ActivityRetainedComponent")

            "dagger.hilt.android.scopes.ActivityScoped" ->
                environment.renderEngine.className("dagger.hilt.android.components", "ActivityComponent")

            "dagger.hilt.android.scopes.FragmentScoped" ->
                environment.renderEngine.className("dagger.hilt.android.components", "FragmentComponent")

            "dagger.hilt.android.scopes.ServiceScoped" ->
                environment.renderEngine.className("dagger.hilt.android.components", "ServiceComponent")

            "dagger.hilt.android.scopes.ViewScoped" ->
                environment.renderEngine.className("dagger.hilt.android.components", "ViewComponent")

            "dagger.hilt.android.scopes.ViewModelScoped" ->
                environment.renderEngine.className("dagger.hilt.android.components", "ViewModelComponent")

            "dagger.hilt.android.scopes.ViewWithFragmentScoped" ->
                environment.renderEngine.className("dagger.hilt.android.components", "ViewWithFragmentComponent")

            else -> null
        }

    private data class ModuleKey<ClassName>(
        val targetType: ClassName,
        val targetComponent: ClassName,
    )

    private class AbortProcessingError : RuntimeException()
}