package se.ansman.dagger.auto.compiler.common.replaces

import se.ansman.dagger.auto.compiler.common.Errors
import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.autobind.AutoBindProcessor
import se.ansman.dagger.auto.compiler.common.autobind.models.AutoBindObjectModule
import se.ansman.dagger.auto.compiler.common.autoinitialize.AutoInitializeProcessor
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.common.processing.error
import se.ansman.dagger.auto.compiler.common.processing.model.ClassDeclaration
import se.ansman.dagger.auto.compiler.common.processing.model.Declaration
import se.ansman.dagger.auto.compiler.common.processing.model.getAnnotation
import se.ansman.dagger.auto.compiler.common.processing.model.getValue
import se.ansman.dagger.auto.compiler.common.processing.model.isAnnotatedWith
import se.ansman.dagger.auto.compiler.common.processing.model.isFullyPublic
import se.ansman.dagger.auto.compiler.common.processing.model.isType
import se.ansman.dagger.auto.compiler.common.processing.rootPeerClass
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.NoOpRenderer
import se.ansman.dagger.auto.compiler.common.rendering.Renderer

class ReplacesProcessor<N, TypeName : Any, ClassName : TypeName, AnnotationSpec, F>(
    override val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: Renderer<AutoBindObjectModule<N, TypeName, ClassName, AnnotationSpec>, F>,
) : Processor<N, TypeName, ClassName, AnnotationSpec> {
    override val logger = environment.logger.withTag("replaces")
    private val autoBindProcessor = AutoBindProcessor(environment, NoOpRenderer, logging = false)
    private val autoInitializeProcessor = AutoInitializeProcessor(environment, NoOpRenderer)
    private val annotation = "se.ansman.dagger.auto.android.testing.Replaces"
    override val annotations: Set<String> = setOf(annotation)

    override fun process(
        elements: Map<String, List<Declaration<N, TypeName, ClassName>>>,
        resolver: AutoDaggerResolver<N, TypeName, ClassName>
    ) {
        logger.info("@Replaces processing started")
        elements.getValue(annotation)
            .forEach { type ->
                require(type is ClassDeclaration)

                if (type.isGeneric) {
                    logger.error(Errors.genericType(annotation), type)
                }
                val annotation = type.getAnnotation(annotation)!!
                val replaces = annotation.getValue<ClassDeclaration<N, TypeName, ClassName>>("type")!!
                logger.info("Processing ${type.className} which replaces ${replaces.className}")

                if (!replaces.isAnnotatedWith(se.ansman.dagger.auto.AutoBind::class)) {
                    logger.error(Errors.Replaces.targetIsNotAutoBind(replaces.className.toString()), type)
                }

                if (replaces.isAnnotatedWith(se.ansman.dagger.auto.AutoInitialize::class)) {
                    logger.info("Type is @AutoInitialize")
                    type.renderAutoInitializeReplacementModule(replaces)
                } else {
                    logger.info("Type is not @AutoInitialize")
                }

                processAutoBind(type, replaces, resolver)
            }
    }

    private fun processAutoBind(
        type: ClassDeclaration<N, TypeName, ClassName>,
        replaces: ClassDeclaration<N, TypeName, ClassName>,
        resolver: AutoDaggerResolver<N, TypeName, ClassName>,
    ) {
        val asType = type.asType()
        autoBindProcessor
            .getAutoBindObjectModule(replaces, resolver) { annotation ->
                val boundTypes = autoBindProcessor.getBoundSupertypes(
                    type = replaces,
                    annotation = annotation
                )
                if (annotation.declaration.isType(se.ansman.dagger.auto.AutoBind::class)) {
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
                    processor = javaClass,
                    moduleName = moduleName,
                    installation = HiltModuleBuilder.Installation.TestInstallIn(
                        components = module.installation.components,
                        replaces = setOf(module.moduleName)
                    ),
                    originatingTopLevelClassName = environment.topLevelClassName(type.className),
                    originatingElement = type.node,
                    type = type.className,
                    isPublic = type.isFullyPublic,
                    isObject = type.kind == ClassDeclaration.Kind.Object,
                    boundTypes = module.boundTypes,
                )
            }
            .map(renderer::render)
            .forEach(environment::write)
    }

    private fun ClassDeclaration<N, TypeName, ClassName>.renderAutoInitializeReplacementModule(
        replaces: ClassDeclaration<N, TypeName, ClassName>,
    ) = with(environment) {
        val autoInitializeModule = autoInitializeProcessor.process(replaces)
        val moduleName = rootPeerClass(className, buildString {
            simpleNames(className).joinTo(this, "")
            append("AutoInitializeReplacementModule")
        })
        logger.info("Rendering $moduleName")
        val file = renderer.render(
            AutoBindObjectModule(
                processor = this@ReplacesProcessor.javaClass,
                moduleName = moduleName,
                installation = HiltModuleBuilder.Installation.TestInstallIn(
                    components = autoInitializeModule.installation.components,
                    replaces = setOf(autoInitializeModule.moduleName)
                ),
                originatingElement = node,
                originatingTopLevelClassName = className,
                type = className,
                isPublic = isFullyPublic,
                isObject = kind == ClassDeclaration.Kind.Object,
                boundTypes = emptyList(),
            )
        )
        environment.write(file)
    }
}