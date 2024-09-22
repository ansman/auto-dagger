package se.ansman.dagger.auto.compiler.kapt

import com.google.auto.common.BasicAnnotationProcessor
import com.google.common.collect.ImmutableSetMultimap
import se.ansman.dagger.auto.compiler.kapt.processing.KaptEnvironment
import se.ansman.dagger.auto.compiler.kapt.processing.KaptResolver
import javax.lang.model.element.Element

class AutoDaggerProcessorStep(
    private val environment: KaptEnvironment,
    private val processor: KaptProcessor
) : BasicAnnotationProcessor.Step {
    override fun annotations(): Set<String> = processor.annotations

    override fun process(elementsByAnnotation: ImmutableSetMultimap<String, Element>): Set<Element> {
        processor.process(KaptResolver(environment, elementsByAnnotation))
        return emptySet()
    }
}