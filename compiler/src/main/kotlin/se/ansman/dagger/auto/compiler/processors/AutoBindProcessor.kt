package se.ansman.dagger.auto.compiler.processors

import dagger.MapKey
import dagger.Reusable
import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.AutoBind
import se.ansman.dagger.auto.AutoBindIntoMap
import se.ansman.dagger.auto.AutoBindIntoSet
import se.ansman.dagger.auto.AutoInitialize
import se.ansman.dagger.auto.BindGenericAs
import se.ansman.dagger.auto.Initializable
import se.ansman.dagger.auto.android.testing.Replaces
import se.ansman.dagger.auto.compiler.Errors
import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.deleteSuffix
import se.ansman.dagger.auto.compiler.common.processing.AnnotationModel
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration
import se.ansman.dagger.auto.compiler.common.processing.Type
import se.ansman.dagger.auto.compiler.common.processing.error
import se.ansman.dagger.auto.compiler.common.processing.getAnnotation
import se.ansman.dagger.auto.compiler.common.processing.getQualifiers
import se.ansman.dagger.auto.compiler.common.processing.getValue
import se.ansman.dagger.auto.compiler.common.processing.isAnnotatedWith
import se.ansman.dagger.auto.compiler.common.processing.isFullyPublic
import se.ansman.dagger.auto.compiler.common.processing.rootPeerClass
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.Renderer
import se.ansman.dagger.auto.compiler.models.autobind.AutoBindObjectModule
import se.ansman.dagger.auto.compiler.models.autobind.AutoBindType
import se.ansman.dagger.auto.compiler.utils.ComponentValidator.validateComponent
import javax.inject.Scope
import javax.inject.Singleton

class AutoBindProcessor<N, TypeName : Any, ClassName : TypeName, AnnotationSpec, F>(
    private val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: Renderer<AutoBindObjectModule<N, TypeName, ClassName, AnnotationSpec>, F>,
    private val logging: Boolean = true,
) : Processor<N, TypeName, ClassName, AnnotationSpec> {
    private val logger = environment.logger.withTag("auto-bind").takeIf { logging }
    override val annotations: Set<String> = setOf(
        AutoBind::class.java.name,
        AutoBindIntoSet::class.java.name,
        AutoBindIntoMap::class.java.name,
    )

    override fun process(resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>) {
        logger?.info("AutoBind processing started")
        annotations.asSequence()
            .onEach { logger?.info("Looking for annotation $it") }
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
        resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>,
    ) {
        logger?.info("Processing $className")
        if (isAnnotatedWith(Replaces::class)) {
            logger?.error(Errors.Replaces.isAutoBindOrInitialize, node)
            return
        }
        if (isGeneric) {
            logger?.error(Errors.genericType(this@AutoBindProcessor.annotations.first(::isAnnotatedWith)), this)
            return
        }

        val objects = getAutoBindObjectModule(this, resolver)
        logger?.info("Found ${objects.size} for $className")
        objects
            .asSequence()
            .map(renderer::render)
            .forEach(environment::write)
    }

    fun getAutoBindObjectModule(
        type: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>,
        resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>,
        getBoundTypes: (AnnotationModel<ClassName, AnnotationSpec>) -> Iterable<Type<N, TypeName, ClassName, AnnotationSpec>> = {
            getBoundSupertypes(type, it)
        },
    ): Collection<AutoBindObjectModule<N, TypeName, ClassName, AnnotationSpec>> {
        val modules: MutableMap<ModuleKey<ClassName>, AutoBindObjectModule<N, TypeName, ClassName, AnnotationSpec>> =
            mutableMapOf()

        type.getAnnotation(AutoBind::class)?.let { annotation ->
            process(
                type = type,
                resolver = resolver,
                mode = HiltModuleBuilder.ProviderMode.Single,
                annotation = annotation,
                output = modules,
                getBoundTypes = getBoundTypes,
            )
        }

        type.getAnnotation(AutoBindIntoSet::class)?.let { annotation ->
            process(
                type = type,
                resolver = resolver,
                mode = HiltModuleBuilder.ProviderMode.IntoSet,
                annotation = annotation,
                output = modules,
                getBoundTypes = getBoundTypes,
            )
        }

        type.getAnnotation(AutoBindIntoMap::class)?.let { annotation ->
            val bindingKeys = type.annotations
                .filter { it.isAnnotatedWith(MapKey::class) }

            when (bindingKeys.size) {
                0 -> logger?.error(Errors.AutoBind.missingBindingKey, type)

                1 -> process(
                    type = type,
                    resolver = resolver,
                    mode = HiltModuleBuilder.ProviderMode.IntoMap(bindingKeys.single().toAnnotationSpec()),
                    annotation = annotation,
                    output = modules,
                    getBoundTypes = getBoundTypes,
                )

                else -> logger?.error(Errors.AutoBind.multipleBindingKeys, type)
            }
        }
        return modules.values
    }

    private fun process(
        type: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>,
        resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>,
        mode: HiltModuleBuilder.ProviderMode<AnnotationSpec>,
        annotation: AnnotationModel<ClassName, AnnotationSpec>,
        output: MutableMap<ModuleKey<ClassName>, AutoBindObjectModule<N, TypeName, ClassName, AnnotationSpec>>,
        getBoundTypes: (AnnotationModel<ClassName, AnnotationSpec>) -> Iterable<Type<N, TypeName, ClassName, AnnotationSpec>>,
    ) {
        logger?.info("Processing annotation ${annotation.qualifiedName} for ${type.className}")

        val bindGenericAs = annotation.getValue("bindGenericAs") ?: BindGenericAs.Type

        val component = type.getTargetComponent(resolver, annotation)
        val key = ModuleKey(targetType = type.className, targetComponent = component)
        val qualifiers = type.getQualifiers()
        val types = getBoundTypes(annotation)
            .also { boundTypes ->
                if (bindGenericAs != BindGenericAs.Type && boundTypes.none { it.isGeneric }) {
                    logger?.error(Errors.AutoBind.invalidBindMode(bindGenericAs), type)
                }
            }
            .asSequence()
            .flatMap { boundType ->
                val typeName = boundType.toTypeName()
                when (bindGenericAs) {
                    BindGenericAs.Type ->
                        sequenceOf(typeName)

                    BindGenericAs.Wildcard ->
                        sequenceOf(resolver.environment.renderEngine.asWildcard(typeName))

                    BindGenericAs.TypeAndWildcard ->
                        sequenceOf(typeName, resolver.environment.renderEngine.asWildcard(typeName))
                }
            }
            .distinct()
            .map { AutoBindType(it, mode, qualifiers) }
            .toList()
        output[key] = output[key]
            ?.withTypesAdded(types)
            ?: AutoBindObjectModule(
                moduleName = getModuleName(type, component),
                installation = HiltModuleBuilder.Installation.InstallIn(component),
                originatingTopLevelClassName = environment.renderEngine.topLevelClassName(type.className),
                originatingElement = type.node,
                type = type.className,
                isPublic = type.isFullyPublic,
                isObject = type.isObject,
                boundTypes = types,
            )
    }

    fun getModuleName(type: ClassDeclaration<*, *, ClassName, *>, component: ClassName): ClassName =
        with(environment.renderEngine) {
            rootPeerClass(type.className, buildString {
                append("AutoBind")
                simpleNames(type.className).joinTo(this, "")
                simpleNames(component).joinTo(this, "")
                deleteSuffix("Component")
                append("Module")
            })
        }

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.getTargetComponent(
        resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>,
        annotation: AnnotationModel<ClassName, AnnotationSpec>,
    ): ClassName =
        annotation.getValue<ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>>("inComponent")
            ?.also { validateComponent(resolver, it) }
            ?.className
            ?: guessComponent()

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.guessComponent(): ClassName {
        val scope = annotations.find { it.isAnnotatedWith(Scope::class) }
            ?: return environment.renderEngine.className(SingletonComponent::class)
        return scope.guessComponent() ?: run {
            logger?.error(Errors.AutoBind.nonStandardScope(scope.qualifiedName), this)
            throw AbortProcessingError()
        }
    }

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.validateComponent(
        resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>,
        component: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>,
    ) {
        if (!component.validateComponent(this, logger)){
            return
        }
        val scope = annotations.find { it.isAnnotatedWith(Scope::class) }
            ?: return
        val inferredComponent = scope.guessComponent() ?: return
        if (isComponentChildComponent(resolver, component, inferredComponent)) {
            logger?.error(
                message = Errors.AutoBind.parentComponent(
                    installIn = resolver.environment.renderEngine.simpleName(component.className),
                    inferredComponent = resolver.environment.renderEngine.simpleName(inferredComponent)
                ),
                node = this
            )
        }
    }

    private fun isComponentChildComponent(
        resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>,
        installInComponent: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>,
        inferredComponent: ClassName,
    ): Boolean {
        var c = inferredComponent
        do {
            c = resolver.lookupType(c)
                .getAnnotation(DefineComponent::class)
                ?.getValue<ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>>("parent")
                ?.className
                ?: return false
        } while (c != installInComponent.className)
        return true
    }

    fun getBoundSupertypes(
        type: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>,
        annotation: AnnotationModel<ClassName, AnnotationSpec>,
    ): Collection<Type<N, TypeName, ClassName, AnnotationSpec>> {
        val asTypes = annotation.getValue<List<ClassDeclaration<*, *, ClassName, *>>>("asTypes")
            ?.takeUnless { it.isEmpty() }
            ?.mapTo(mutableSetOf()) { it.className }
            ?: return type.supertypes
                // If a type is annotated with @AutoInitialize, we exclude Initializable
                .filterNot {
                    it.toTypeName() == environment.renderEngine.className(Initializable::class) &&
                            type.isAnnotatedWith(AutoInitialize::class)
                }
                .also {
                    when (it.size) {
                        0 -> logger?.error(Errors.AutoBind.noSuperTypes, type)
                        1 -> {
                            /* OK */
                        }

                        else -> logger?.error(Errors.AutoBind.multipleSuperTypes, type)
                    }
                }

        val supertypes = type.supertypes.associateByTo(mutableMapOf()) {
            environment.renderEngine.rawType(it.toTypeName())
        }
        supertypes.keys.retainAll(asTypes)
        val missingTypes = asTypes - supertypes.keys
        for (missingType in missingTypes) {
            val error = if (type.asType().isAssignableTo(missingType)) {
                Errors.AutoBind::missingDirectSuperType
            } else {
                Errors.AutoBind::missingBoundType
            }
            logger?.error(error(missingType.toString()), type)
        }
        return supertypes.values
    }

    private fun AnnotationModel<*, *>.guessComponent(): ClassName? =
        when (qualifiedName) {
            Singleton::class.java.name,
            Reusable::class.java.name ->
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

    data class ModuleKey<ClassName>(
        val targetType: ClassName,
        val targetComponent: ClassName,
    )

    private class AbortProcessingError : RuntimeException()
}
