package se.ansman.dagger.auto.compiler.optionallyprovided

import se.ansman.dagger.auto.OptionallyProvided
import se.ansman.dagger.auto.compiler.Errors
import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.deleteSuffix
import se.ansman.dagger.auto.compiler.common.processing.*
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.Renderer
import se.ansman.dagger.auto.compiler.optionallyprovided.models.OptionallyProvidedObjectModule
import se.ansman.dagger.auto.compiler.utils.getTargetComponent

class OptionallyProvidedProcessor<N, TypeName : Any, ClassName : TypeName, AnnotationSpec, F>(
    override val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: Renderer<OptionallyProvidedObjectModule<N, TypeName, ClassName, AnnotationSpec>, F>,
) : Processor<N, TypeName, ClassName, AnnotationSpec> {
    override val logger = environment.logger.withTag("optionally-provided")
    private val annotation = OptionallyProvided::class.java.canonicalName

    override val annotations: Set<String> = setOf(annotation)

    override fun process(resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>) {
        logger.info("OptionallyProvided processing started")
        resolver.nodesAnnotatedWith(annotation)
            .map { it as ClassDeclaration }
            .forEach { node -> node.process(resolver) }
        logger.info("OptionallyProvided processing finished")
    }

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.process(
        resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>,
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

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.getModule(
        resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>,
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
            qualifiers = getQualifiers()
        )
    }

    private fun getModuleName(type: ClassDeclaration<*, *, ClassName, *>, component: ClassName): ClassName =
        with(environment) {
            rootPeerClass(type.className, buildString {
                append("OptionallyProvided")
                simpleNames(type.className).joinTo(this, "")
                simpleNames(component).joinTo(this, "")
                deleteSuffix("Component")
                append("Module")
            })
        }

    private val ClassDeclaration<*, *, ClassName, *>.isObject: Boolean
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