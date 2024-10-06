package se.ansman.dagger.auto.compiler.common.retrofit

import dagger.Reusable
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.compiler.common.Errors
import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerLogger
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.processing.error
import se.ansman.dagger.auto.compiler.common.processing.info
import se.ansman.dagger.auto.compiler.common.processing.model.ClassDeclaration
import se.ansman.dagger.auto.compiler.common.processing.model.ClassDeclaration.Kind
import se.ansman.dagger.auto.compiler.common.processing.model.Declaration
import se.ansman.dagger.auto.compiler.common.processing.model.FunctionDeclaration
import se.ansman.dagger.auto.compiler.common.processing.model.PropertyDeclaration
import se.ansman.dagger.auto.compiler.common.processing.model.getAnnotation
import se.ansman.dagger.auto.compiler.common.processing.model.getQualifiers
import se.ansman.dagger.auto.compiler.common.processing.model.getValue
import se.ansman.dagger.auto.compiler.common.processing.model.isAnnotatedWith
import se.ansman.dagger.auto.compiler.common.processing.model.isFullyPrivate
import se.ansman.dagger.auto.compiler.common.processing.model.isFullyPublic
import se.ansman.dagger.auto.compiler.common.processing.model.isType
import se.ansman.dagger.auto.compiler.common.processing.rootPeerClass
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.retrofit.models.ApiServiceModule
import se.ansman.dagger.auto.compiler.common.retrofit.renderer.BaseApiServiceModuleRenderer
import se.ansman.dagger.auto.compiler.common.utils.validateComponent
import javax.inject.Scope

abstract class BaseApiServiceProcessor<N, TypeName, ClassName : TypeName, AnnotationSpec, F>(
    override val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: BaseApiServiceModuleRenderer<N, TypeName, ClassName, AnnotationSpec, *, *, *, F>,
    private val annotation: String,
    serviceAnnotations: Set<String>,
    private val modulePrefix: String,
    override val logger: AutoDaggerLogger<N>,
    private val errors: Errors.ApiService,
) : Processor<N, TypeName, ClassName, AnnotationSpec>,
    RenderEngine<N, TypeName, ClassName, AnnotationSpec> by environment {
    override val annotations: Set<String> = setOf(annotation)

    private val serviceAnnotations by lazy {
        serviceAnnotations.map { environment.className(it) }
    }

    override fun process(
        elements: Map<String, List<Declaration<N, TypeName, ClassName>>>,
        resolver: AutoDaggerResolver<N, TypeName, ClassName>
    ) {
        logger.info("@AutoProvideService processing started")
        elements.getValue(annotation)
            .map { it as ClassDeclaration<N, TypeName, ClassName> }
            .map { service ->
                logger.info("Processing ${service.className}")
                val targetComponent = service
                    .getAnnotation(annotation)!!
                    .getValue<ClassDeclaration<N, TypeName, ClassName>>("inComponent")
                    ?: resolver.lookupType(SingletonComponent::class)

                service.validateService()
                validateComponent(service, resolver, targetComponent)

                ApiServiceModule(
                    processor = javaClass,
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
                    qualifiers = service.getQualifiers().toAnnotationSpecs().toSet(),
                    scope = service.findScope(targetComponent)
                )
            }
            .map(renderer::render)
            .forEach(environment::write)
    }

    private fun ClassDeclaration<N, TypeName, ClassName>.validateService() {
        if (kind != Kind.Interface) logger.error(errors.nonInterface, node)
        if (isGeneric) logger.error(Errors.genericType(annotation), node)
        if (isFullyPrivate) logger.error(errors.privateType, node)
        if (declarations.isEmpty()) logger.error(errors.emptyService, node)
        for (node in declarations) {
            logger.info("Validating enclosed element", node)
            when (node) {
                is FunctionDeclaration<N, TypeName, ClassName> -> if (serviceAnnotations.none(node::isAnnotatedWith)) {
                    logger.error(errors.invalidServiceMethod, node)
                }

                is PropertyDeclaration<N, TypeName, ClassName> -> logger.error(errors.propertiesNotAllowed, node)

                is ClassDeclaration -> error("Unexpected node $node")
            }
        }
    }

    private fun ClassDeclaration<N, TypeName, ClassName>.findScope(
        targetComponent: ClassDeclaration<N, TypeName, ClassName>
    ): AnnotationSpec? =
        annotations
            .filter { annotation ->
                when {
                    annotation.declaration.isType(Reusable::class) -> true
                    annotation.isAnnotatedWith(Scope::class) -> {
                        // This will have logged and error about an invalid component
                        val neededScope = targetComponent.annotations
                            .find { it.isAnnotatedWith(Scope::class) }
                            ?: return@filter false

                        if (neededScope.declaration.className != annotation.declaration.className) {
                            logger.error(
                                message = errors.invalidScope(
                                    scope = annotation.declaration.name,
                                    component = environment.simpleName(targetComponent.className),
                                    neededScope = neededScope.declaration.name
                                ),
                                node = node
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