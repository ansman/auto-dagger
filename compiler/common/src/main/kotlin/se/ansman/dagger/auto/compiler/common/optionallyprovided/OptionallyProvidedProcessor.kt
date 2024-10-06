package se.ansman.dagger.auto.compiler.common.optionallyprovided

import se.ansman.dagger.auto.OptionallyProvided
import se.ansman.dagger.auto.compiler.common.Errors
import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.deleteSuffix
import se.ansman.dagger.auto.compiler.common.get
import se.ansman.dagger.auto.compiler.common.optionallyprovided.models.OptionallyProvidedObjectModule
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.processing.error
import se.ansman.dagger.auto.compiler.common.processing.model.ClassDeclaration
import se.ansman.dagger.auto.compiler.common.processing.model.Declaration
import se.ansman.dagger.auto.compiler.common.processing.model.getAnnotation
import se.ansman.dagger.auto.compiler.common.processing.model.getQualifiers
import se.ansman.dagger.auto.compiler.common.processing.model.isFullyPublic
import se.ansman.dagger.auto.compiler.common.processing.rootPeerClass
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.Renderer
import se.ansman.dagger.auto.compiler.common.utils.getTargetComponent

class OptionallyProvidedProcessor<N, TypeName : Any, ClassName : TypeName, AnnotationSpec, F>(
    override val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: Renderer<OptionallyProvidedObjectModule<N, TypeName, ClassName, AnnotationSpec>, F>,
) : Processor<N, TypeName, ClassName, AnnotationSpec>,
    RenderEngine<N, TypeName, ClassName, AnnotationSpec> by environment {
    override val logger = environment.logger.withTag("optionally-provided")
    private val annotation = OptionallyProvided::class

    override val annotations: Set<String> = setOf(annotation.java.canonicalName)

    override fun process(
        elements: Map<String, List<Declaration<N, TypeName, ClassName>>>,
        resolver: AutoDaggerResolver<N, TypeName, ClassName>
    ) {
        logger.info("OptionallyProvided processing started")
        elements[annotation]
            .map { it as ClassDeclaration }
            .forEach { node -> node.process(resolver) }
        logger.info("OptionallyProvided processing finished")
    }

    private fun ClassDeclaration<N, TypeName, ClassName>.process(
        resolver: AutoDaggerResolver<N, TypeName, ClassName>,
    ) {
        logger.info("Processing $className")
        if (isGeneric) {
            logger.error(Errors.genericType(annotation), this)
            return
        }
        if (isObject) {
            logger.error(Errors.OptionallyProvided.objectType, this)
            return
        }

        val module = getModule(resolver)
            ?: return
        val file = renderer.render(module)
        environment.write(file)
    }

    private fun ClassDeclaration<N, TypeName, ClassName>.getModule(
        resolver: AutoDaggerResolver<N, TypeName, ClassName>,
    ): OptionallyProvidedObjectModule<N, TypeName, ClassName, AnnotationSpec>? {
        val annotation = getAnnotation(annotation)!!
        val component = getTargetComponent(this, resolver, annotation)
            ?: return null
        return OptionallyProvidedObjectModule(
            processor = this@OptionallyProvidedProcessor.javaClass,
            moduleName = getModuleName(this, component),
            installation = HiltModuleBuilder.Installation.InstallIn(component),
            originatingTopLevelClassName = environment.topLevelClassName(className),
            originatingElement = node,
            type = className,
            isPublic = isFullyPublic,
            qualifiers = getQualifiers().toAnnotationSpecs().toSet()
        )
    }

    private fun getModuleName(type: ClassDeclaration<*, *, ClassName>, component: ClassName): ClassName =
        with(environment) {
            rootPeerClass(type.className, buildString {
                append("OptionallyProvided")
                simpleNames(type.className).joinTo(this, "")
                simpleNames(component).joinTo(this, "")
                deleteSuffix("Component")
                append("Module")
            })
        }

    private val ClassDeclaration<*, *, ClassName>.isObject: Boolean
        get() = when (kind) {
            ClassDeclaration.Kind.Object,
            ClassDeclaration.Kind.CompanionObject -> true

            ClassDeclaration.Kind.Class,
            ClassDeclaration.Kind.Interface,
            ClassDeclaration.Kind.EnumClass,
            ClassDeclaration.Kind.EnumEntry,
            ClassDeclaration.Kind.AnnotationClass -> false
        }
}