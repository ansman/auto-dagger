package se.ansman.dagger.auto.compiler.kapt

import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.common.collect.ImmutableSetMultimap
import se.ansman.dagger.auto.compiler.kapt.processing.KaptEnvironment
import se.ansman.dagger.auto.compiler.kapt.processing.KaptResolver
import se.ansman.dagger.auto.compiler.kapt.processing.model.KaptClassDeclaration
import se.ansman.dagger.auto.compiler.kapt.processing.model.KaptFunctionDeclaration
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

class AutoDaggerProcessorStep(
    private val environment: KaptEnvironment,
    private val resolver: KaptResolver,
    private val processor: KaptProcessor,
) : BasicAnnotationProcessor.Step {
    override fun annotations(): Set<String> = processor.annotations

    @Suppress("UnstableApiUsage")
    override fun process(elementsByAnnotation: ImmutableSetMultimap<String, Element>): Set<Element> {
        processor.process(
            elementsByAnnotation.asMap()
                .mapValues { (_, elements) ->
                    elements.mapNotNull { element ->
                        when {
                            MoreElements.isType(element) ->
                                KaptClassDeclaration(MoreElements.asType(element), resolver)

                            element.kind == ElementKind.METHOD || element.kind == ElementKind.CONSTRUCTOR ->
                                KaptFunctionDeclaration(MoreElements.asExecutable(element), resolver)

                            else -> {
                                environment.logger.error("Unknown element kind ${element.kind}", element)
                                null
                            }
                        }
                    }
                }
                .withDefault { emptyList() },
            resolver = resolver,
        )
        return emptySet()
    }
}