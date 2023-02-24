package se.ansman.dagger.auto.kapt

import com.google.auto.common.BasicAnnotationProcessor
import com.google.common.collect.ImmutableSetMultimap
import se.ansman.dagger.auto.AutoInitialize
import se.ansman.dagger.auto.kapt.processing.KaptProcessing
import se.ansman.dagger.auto.processors.AutoInitializeProcessor
import se.ansman.dagger.auto.renderers.JavaAutoInitializeModuleRenderer
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class AutoInitializeStep(
    private val processingEnv: ProcessingEnvironment,
) : BasicAnnotationProcessor.Step {
    override fun annotations(): Set<String> = setOf(AutoInitialize::class.java.canonicalName)

    override fun process(elementsByAnnotation: ImmutableSetMultimap<String, Element>): Set<Element> {
        val processing = KaptProcessing(processingEnv, elementsByAnnotation)
        val processor = AutoInitializeProcessor(
            processing = processing,
            renderer = JavaAutoInitializeModuleRenderer
        )
        processor.process()
        return emptySet()
    }
}