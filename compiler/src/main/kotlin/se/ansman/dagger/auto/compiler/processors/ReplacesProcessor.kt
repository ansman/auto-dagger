package se.ansman.dagger.auto.compiler.processors

import se.ansman.dagger.auto.AutoBind
import se.ansman.dagger.auto.AutoInitialize
import se.ansman.dagger.auto.android.testing.Replaces
import se.ansman.dagger.auto.compiler.Errors
import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration
import se.ansman.dagger.auto.compiler.common.processing.error
import se.ansman.dagger.auto.compiler.common.processing.getAnnotation
import se.ansman.dagger.auto.compiler.common.processing.getValue
import se.ansman.dagger.auto.compiler.common.processing.isAnnotatedWith
import se.ansman.dagger.auto.compiler.common.processing.isFullyPublic
import se.ansman.dagger.auto.compiler.common.processing.nodesAnnotatedWith
import se.ansman.dagger.auto.compiler.common.processing.rootPeerClass
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.NoOpRenderer
import se.ansman.dagger.auto.compiler.common.rendering.Renderer
import se.ansman.dagger.auto.compiler.models.autobind.AutoBindObjectModule

class ReplacesProcessor<N, TypeName : Any, ClassName : TypeName, AnnotationSpec, F>(
    private val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: Renderer<AutoBindObjectModule<N, TypeName, ClassName, AnnotationSpec>, F>,
) : Processor<N, TypeName, ClassName, AnnotationSpec> {
    private val logger = environment.logger.withTag("replaces")
    private val autoBindProcessor = AutoBindProcessor(environment, NoOpRenderer, logging = false)
    private val autoInitializeProcessor = AutoInitializeProcessor(environment, NoOpRenderer)

    override val annotations: Set<String> = setOf(Replaces::class.java.name)

    override fun process(resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>) {
        logger.info("@Replaces processing started")
        resolver.nodesAnnotatedWith(Replaces::class)
            .map { it as ClassDeclaration }
            .forEach { type ->
                if (type.isGeneric) {
                    logger.error(Errors.genericType(Replaces::class), type)
                }
                val annotation = type.getAnnotation(Replaces::class)!!
                val replaces = annotation.getValue<ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>>("type")!!
                logger.info("Processing ${type.className} which replaces ${replaces.className}")

                if (!replaces.isAnnotatedWith(AutoBind::class)) {
                    logger.error(Errors.Replaces.targetIsNotAutoBind(replaces.className.toString()), type)
                }

                if (replaces.isAnnotatedWith(AutoInitialize::class)) {
                    logger.info("Type is @AutoInitialize")
                    type.renderAutoInitializeReplacementModule(replaces)
                } else {
                    logger.info("Type is not @AutoInitialize")
                }

                processAutoBind(type, replaces, resolver)
            }
    }

    private fun processAutoBind(
        type: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>,
        replaces: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>,
        resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>,
    ) {
        val asType = type.asType()
        autoBindProcessor
            .getAutoBindObjectModule(replaces, resolver) { annotation ->
                val boundTypes = autoBindProcessor.getBoundSupertypes(
                    type = replaces,
                    annotation = annotation
                )
                if (annotation.isOfType(AutoBind::class)) {
                    boundTypes.onEach {
                        if (!asType.isAssignableTo(it)) {
                            logger.error(
                                Errors.Replaces.missingBoundType(
                                    replacedObject = replaces.className.toString(),
                                    boundType = it.toTypeName().toString(),
                                    type = type.className.toString()
                                ),
                                type
                            )
                        }
                    }
                } else {
                    boundTypes.filter { asType.isAssignableTo(it) }
                }

            }
            .also { modules ->
                logger.info("${replaces.className} produces modules ${modules.joinToString { it.moduleName.toString() }}")
            }
            .asSequence()
            .map { module ->
                val moduleName = autoBindProcessor.getModuleName(
                    type,
                    module.installation.components.single()
                )
                logger.info("Generating replacement for ${module.moduleName} named $moduleName")
                AutoBindObjectModule(
                    moduleName = moduleName,
                    installation = HiltModuleBuilder.Installation.TestInstallIn(
                        components = module.installation.components,
                        replaces = setOf(module.moduleName)
                    ),
                    originatingTopLevelClassName = environment.renderEngine.topLevelClassName(type.className),
                    originatingElement = type.node,
                    type = type.className,
                    isPublic = type.isFullyPublic,
                    isObject = type.isObject,
                    boundTypes = module.boundTypes,
                )
            }
            .map(renderer::render)
            .forEach(environment::write)
    }

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.renderAutoInitializeReplacementModule(
        replaces: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>,
    ) = with(environment.renderEngine) {
        val autoInitializeModule = autoInitializeProcessor.process(replaces)
        val moduleName = rootPeerClass(className, buildString {
            simpleNames(className).joinTo(this, "")
            append("AutoInitializeReplacementModule")
        })
        logger.info("Rendering $moduleName")
        val file = renderer.render(
            AutoBindObjectModule(
                moduleName = moduleName,
                installation = HiltModuleBuilder.Installation.TestInstallIn(
                    components = autoInitializeModule.installation.components,
                    replaces = setOf(autoInitializeModule.moduleName)
                ),
                originatingElement = node,
                originatingTopLevelClassName = className,
                type = className,
                isPublic = isFullyPublic,
                isObject = isObject,
                boundTypes = emptyList(),
            )
        )
        environment.write(file)
    }
}
