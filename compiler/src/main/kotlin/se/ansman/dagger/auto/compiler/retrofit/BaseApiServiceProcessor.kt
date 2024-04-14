package se.ansman.dagger.auto.compiler.retrofit

import dagger.Reusable
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.compiler.Errors
import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerLogger
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration
import se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration.Kind
import se.ansman.dagger.auto.compiler.common.processing.Function
import se.ansman.dagger.auto.compiler.common.processing.Property
import se.ansman.dagger.auto.compiler.common.processing.getAnnotation
import se.ansman.dagger.auto.compiler.common.processing.getQualifiers
import se.ansman.dagger.auto.compiler.common.processing.getValue
import se.ansman.dagger.auto.compiler.common.processing.isAnnotatedWith
import se.ansman.dagger.auto.compiler.common.processing.isFullyPrivate
import se.ansman.dagger.auto.compiler.common.processing.isFullyPublic
import se.ansman.dagger.auto.compiler.common.processing.lookupType
import se.ansman.dagger.auto.compiler.common.processing.nodesAnnotatedWith
import se.ansman.dagger.auto.compiler.common.processing.rootPeerClass
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.retrofit.models.ApiServiceModule
import se.ansman.dagger.auto.compiler.retrofit.renderer.BaseApiServiceModuleRenderer
import se.ansman.dagger.auto.compiler.utils.ComponentValidator.validateComponent
import javax.inject.Scope
import kotlin.reflect.KClass

abstract class BaseApiServiceProcessor<N, TypeName, ClassName : TypeName, AnnotationSpec, F>(
    private val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: BaseApiServiceModuleRenderer<N, TypeName, ClassName, AnnotationSpec, *, *, F>,
    private val annotation: KClass<out Annotation>,
    serviceAnnotations: Set<String>,
    private val modulePrefix: String,
    private val logger: AutoDaggerLogger<N>,
    private val errors: Errors.ApiService,
) : Processor<N, TypeName, ClassName, AnnotationSpec> {
    override val annotations: Set<String> = setOf(annotation.java.canonicalName)

    private val serviceAnnotations by lazy {
        serviceAnnotations.map { environment.className(it) }
    }

    override fun process(resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>) {
        logger.info("@AutoProvideService processing started")
        resolver.nodesAnnotatedWith(annotation)
            .map { it as ClassDeclaration<N, TypeName, ClassName, AnnotationSpec> }
            .map { service ->
                logger.info("Processing ${service.className}")
                val targetComponent = service
                    .getAnnotation(annotation)!!
                    .getValue<ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>>("inComponent")
                    ?: resolver.lookupType(SingletonComponent::class)

                service.validateService()
                targetComponent.validateComponent(service, logger)

                ApiServiceModule(
                    moduleName = environment.rootPeerClass(
                        service.className,
                        environment.simpleNames(service.className).joinToString(
                            prefix = modulePrefix,
                            separator = ""
                        )
                    ),
                    installation = HiltModuleBuilder.Installation.InstallIn(targetComponent.className),
                    originatingTopLevelClassName = environment.topLevelClassName(service.className),
                    originatingElement = service.node,
                    serviceClass = service.className,
                    isPublic = service.isFullyPublic,
                    qualifiers = service.getQualifiers(),
                    scope = service.findScope(targetComponent)
                )
            }
            .map(renderer::render)
            .forEach(environment::write)
    }

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.validateService() {
        if (kind != Kind.Interface) logger.error(errors.nonInterface, node)
        if (isGeneric) logger.error(Errors.genericType(annotation), node)
        if (isFullyPrivate) logger.error(errors.privateType, node)
        if (declaredNodes.isEmpty()) logger.error(errors.emptyService, node)
        for (node in declaredNodes) {
            logger.info("Validating enclosed element $node")
            when (node) {
                is Function<N, TypeName, ClassName, AnnotationSpec> -> if (serviceAnnotations.none(node::isAnnotatedWith)) {
                    logger.error(errors.invalidServiceMethod, node.node)
                }

                is Property<N, TypeName, ClassName, AnnotationSpec> -> logger.error(
                    errors.propertiesNotAllowed,
                    node.node
                )

                else -> error("Unexpected node: $node")
            }
        }
    }

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.findScope(
        targetComponent: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>
    ): AnnotationSpec? =
        annotations
            .filter { annotation ->
                when {
                    annotation.isOfType(Reusable::class) -> true
                    annotation.isAnnotatedWith(Scope::class) -> {
                        // This will have logged and error about an invalid component
                        val neededScope = targetComponent.annotations
                            .find { it.isAnnotatedWith(Scope::class) }
                            ?: return@filter false

                        if (neededScope.className != annotation.className) {
                            logger.error(
                                errors.invalidScope(
                                    scope = annotation.simpleName,
                                    component = environment.simpleName(targetComponent.className),
                                    neededScope = neededScope.simpleName
                                ), node
                            )
                            return@filter false
                        }
                        true
                    }

                    else -> false
                }
            }
            .let {
                when (it.size) {
                    0 -> null
                    1 -> it.single()
                    else -> {
                        logger.error(errors.scopeAndReusable, node)
                        null
                    }
                }
            }
            ?.toAnnotationSpec()
}