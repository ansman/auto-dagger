package se.ansman.dagger.auto.compiler.common.autobind

import dagger.MapKey
import se.ansman.dagger.auto.AutoBind
import se.ansman.dagger.auto.AutoBindIntoMap
import se.ansman.dagger.auto.AutoBindIntoSet
import se.ansman.dagger.auto.AutoInitialize
import se.ansman.dagger.auto.BindGenericAs
import se.ansman.dagger.auto.Initializable
import se.ansman.dagger.auto.compiler.common.Errors
import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.autobind.models.AutoBindObjectModule
import se.ansman.dagger.auto.compiler.common.autobind.models.AutoBindType
import se.ansman.dagger.auto.compiler.common.deleteSuffix
import se.ansman.dagger.auto.compiler.common.get
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.processing.error
import se.ansman.dagger.auto.compiler.common.processing.model.AnnotationNode
import se.ansman.dagger.auto.compiler.common.processing.model.ClassDeclaration
import se.ansman.dagger.auto.compiler.common.processing.model.ClassDeclaration.Kind
import se.ansman.dagger.auto.compiler.common.processing.model.Declaration
import se.ansman.dagger.auto.compiler.common.processing.model.Type
import se.ansman.dagger.auto.compiler.common.processing.model.getAnnotation
import se.ansman.dagger.auto.compiler.common.processing.model.getClassArrayValue
import se.ansman.dagger.auto.compiler.common.processing.model.getQualifiers
import se.ansman.dagger.auto.compiler.common.processing.model.getValue
import se.ansman.dagger.auto.compiler.common.processing.model.isAnnotatedWith
import se.ansman.dagger.auto.compiler.common.processing.model.isFullyPublic
import se.ansman.dagger.auto.compiler.common.processing.rootPeerClass
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.Renderer
import se.ansman.dagger.auto.compiler.common.utils.getTargetComponent
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class AutoBindProcessor<N, TypeName, ClassName : TypeName, AnnotationSpec, F>(
    override val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: Renderer<AutoBindObjectModule<N, TypeName, ClassName, AnnotationSpec>, F>,
    private val logging: Boolean = true,
) : Processor<N, TypeName, ClassName, AnnotationSpec>,
    RenderEngine<N, TypeName, ClassName, AnnotationSpec> by environment{
    override val logger = environment.logger.withTag("auto-bind").takeIf { logging }
    private val autoBindAnnotations = setOf(
        AutoBind::class.java.canonicalName,
        AutoBindIntoSet::class.java.canonicalName,
        AutoBindIntoMap::class.java.canonicalName,
    )

    override val annotations: Set<String> = autoBindAnnotations + setOf(
        BindGenericAs.Default::class.java.canonicalName
    )

    override fun process(
        elements: Map<String, List<Declaration<N, TypeName, ClassName>>>,
        resolver: AutoDaggerResolver<N, TypeName, ClassName>
    ) {
        logger?.info("AutoBind processing started")
        autoBindAnnotations.asSequence()
            .onEach { logger?.info("Looking for annotation $it") }
            .flatMap { elements.getValue(it) }
            .distinct()
            .map { it as ClassDeclaration }
            .forEach { node ->
                try {
                    node.process(resolver)
                } catch (e: AbortProcessingError) {
                    return@forEach
                }
            }
        logger?.info("Validating BindGenericAs.Default")
        elements[BindGenericAs.Default::class].forEach { node ->
            (node as ClassDeclaration).validateBindGenericAsDefault()
        }
        logger?.info("AutoBind processing finished")
    }

    private fun ClassDeclaration<N, TypeName, ClassName>.validateBindGenericAsDefault() {
        logger?.info("Validating $className")
        if (!isGeneric) {
            logger?.error(Errors.AutoBind.BindGenericAsDefault.nonGenericType, this)
        }
        val isSupportedType = when (kind) {
            Kind.Class -> isAbstract || isSealedClass
            Kind.Interface -> true
            Kind.EnumClass,
            Kind.EnumEntry,
            Kind.AnnotationClass,
            Kind.Object,
            Kind.CompanionObject -> false
        }
        if (!isSupportedType) {
            logger?.error(Errors.AutoBind.BindGenericAsDefault.nonAbstractType, this)
        }
    }

    private fun ClassDeclaration<N, TypeName, ClassName>.process(
        resolver: AutoDaggerResolver<N, TypeName, ClassName>,
    ) {
        logger?.info("Processing $className")
        if (isAnnotatedWith("se.ansman.dagger.auto.android.testing.Replaces")) {
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
        type: ClassDeclaration<N, TypeName, ClassName>,
        resolver: AutoDaggerResolver<N, TypeName, ClassName>,
        getBoundTypes: (AnnotationNode<N, TypeName, ClassName>) -> Iterable<Type<N, TypeName, ClassName>> = {
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
        type: ClassDeclaration<N, TypeName, ClassName>,
        resolver: AutoDaggerResolver<N, TypeName, ClassName>,
        mode: HiltModuleBuilder.ProviderMode<AnnotationSpec>,
        annotation: AnnotationNode<N, TypeName, ClassName>,
        output: MutableMap<ModuleKey<ClassName>, AutoBindObjectModule<N, TypeName, ClassName, AnnotationSpec>>,
        getBoundTypes: (AnnotationNode<N, TypeName, ClassName>) -> Iterable<Type<N, TypeName, ClassName>>,
    ) {
        logger?.info("Processing annotation ${annotation.declaration.className} for ${type.className}")

        val targetTypes = getBoundTypes(annotation)
            .associateBy({ it }) { boundType ->
                val bindGenericAs = annotation.getValue<BindGenericAs>("bindGenericAs")
                    ?: boundType.getDefaultBindGenericAs()
                    ?: BindGenericAs.ExactType

                if (bindGenericAs != BindGenericAs.ExactType && !boundType.isGeneric) {
                    logger?.error(Errors.AutoBind.invalidBindMode(bindGenericAs), type)
                }

                bindGenericAs
            }

        val component = getTargetComponent(type, resolver, annotation)
            ?: throw AbortProcessingError()
        val key = ModuleKey(targetType = type.className, targetComponent = component)
        val qualifiers = type.getQualifiers().toAnnotationSpecs().toSet()

        val boundTypes = targetTypes
            .asSequence()
            .flatMap { (boundType, bindGenericAs) ->
                val typeName = boundType.toTypeName()
                when (bindGenericAs) {
                    BindGenericAs.ExactType ->
                        sequenceOf(typeName)

                    BindGenericAs.Wildcard ->
                        sequenceOf(resolver.environment.asWildcard(typeName))

                    BindGenericAs.ExactTypeAndWildcard ->
                        sequenceOf(typeName, resolver.environment.asWildcard(typeName))
                }
            }
            .distinct()
            .map { AutoBindType(it, mode, qualifiers) }
            .toList()
        output[key] = output[key]
            ?.withTypesAdded(boundTypes)
            ?: AutoBindObjectModule(
                processor = javaClass,
                moduleName = getModuleName(type, component),
                installation = HiltModuleBuilder.Installation.InstallIn(component),
                originatingTopLevelClassName = environment.topLevelClassName(type.className),
                originatingElement = type.node,
                type = type.className,
                isPublic = type.isFullyPublic,
                isObject = type.kind == Kind.Object,
                boundTypes = boundTypes,
            )
    }

    fun getModuleName(type: ClassDeclaration<*, *, ClassName>, component: ClassName): ClassName =
        with(environment) {
            rootPeerClass(type.className, buildString {
                append("AutoBind")
                simpleNames(type.className).joinTo(this, "")
                simpleNames(component).joinTo(this, "")
                deleteSuffix("Component")
                append("Module")
            })
        }

    private fun Type<N, TypeName, ClassName>.getDefaultBindGenericAs(): BindGenericAs? =
        declaration
            ?.getAnnotation(BindGenericAs.Default::class)
            ?.getValue("value")

    fun getBoundSupertypes(
        type: ClassDeclaration<N, TypeName, ClassName>,
        annotation: AnnotationNode<N, TypeName, ClassName>,
    ): Collection<Type<N, TypeName, ClassName>> {
        val asTypes = annotation.getClassArrayValue("asTypes")
            ?.takeUnless { it.isEmpty() }
            ?.mapTo(mutableSetOf()) { it.className }
            ?: return type.superTypes
                // If a type is annotated with @AutoInitialize, we exclude Initializable
                .filterNot {
                    it.toTypeName() == environment.className(Initializable::class) &&
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

        val supertypes = type.superTypes.associateByTo(mutableMapOf()) {
            environment.rawType(it.toTypeName())
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

    data class ModuleKey<ClassName>(
        val targetType: ClassName,
        val targetComponent: ClassName,
    )

    private class AbortProcessingError : RuntimeException()
}
