package se.ansman.dagger.auto.compiler.retrofit

import dagger.Reusable
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.compiler.Errors
import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
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
import se.ansman.dagger.auto.compiler.retrofit.models.RetrofitModule
import se.ansman.dagger.auto.compiler.retrofit.renderer.RetrofitModuleRenderer
import se.ansman.dagger.auto.compiler.utils.ComponentValidator.validateComponent
import se.ansman.dagger.auto.retrofit.AutoProvideService
import javax.inject.Scope

class RetrofitProcessor<N, TypeName, ClassName : TypeName, AnnotationSpec, F>(
    private val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: RetrofitModuleRenderer<N, TypeName, ClassName, AnnotationSpec, *, *, F>,
) : Processor<N, TypeName, ClassName, AnnotationSpec> {
    private val validRetrofitAnnotations by lazy {
        Companion.validRetrofitAnnotations.map { environment.className(it) }
    }
    private val logger = environment.logger.withTag("retrofit")
    override val annotations: Set<String> = setOf(
        AutoProvideService::class.java.canonicalName,
    )

    override fun process(resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>) {
        logger.info("@AutoProvideService processing started")
        resolver.nodesAnnotatedWith(AutoProvideService::class)
            .map { it as ClassDeclaration<N, TypeName, ClassName, AnnotationSpec> }
            .map { service ->
                logger.info("Processing ${service.className}")
                val targetComponent = service
                    .getAnnotation(AutoProvideService::class)!!
                    .getValue<ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>>("inComponent")
                    ?: resolver.lookupType(SingletonComponent::class)

                service.validateService()
                targetComponent.validateComponent(service, logger)

                RetrofitModule(
                    moduleName = environment.rootPeerClass(
                        service.className,
                        environment.simpleNames(service.className).joinToString(
                            prefix = "AutoBindRetrofit",
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
        if (!(kind == Kind.Interface)) logger.error(Errors.Retrofit.nonInterface, node)
        if (isGeneric) logger.error(Errors.genericType(AutoProvideService::class), node)
        if (isFullyPrivate) logger.error(Errors.Retrofit.privateType, node)
        if (declaredNodes.isEmpty()) logger.error(Errors.Retrofit.emptyService, node)
        for (node in declaredNodes) {
            logger.info("Validating enclosed element $node")
            when (node) {
                is Function -> if (validRetrofitAnnotations.none(node::isAnnotatedWith)) {
                    logger.error(Errors.Retrofit.invalidServiceMethod, node.node)
                }

                is Property -> logger.error(Errors.Retrofit.propertiesNotAllowed, node.node)
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
                            logger.error(Errors.Retrofit.invalidScope(
                                scope = annotation.simpleName,
                                component = environment.simpleName(targetComponent.className),
                                neededScope = neededScope.simpleName
                            ), node)
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
                        logger.error(Errors.Retrofit.scopeAndReusable, node)
                        null
                    }
                }
            }
            ?.toAnnotationSpec()

    companion object {
        private val validRetrofitAnnotations = setOf(
            "retrofit2.http.DELETE",
            "retrofit2.http.GET",
            "retrofit2.http.HEAD",
            "retrofit2.http.HTTP",
            "retrofit2.http.OPTIONS",
            "retrofit2.http.PATCH",
            "retrofit2.http.POST",
            "retrofit2.http.PUT",
        )
    }
}
