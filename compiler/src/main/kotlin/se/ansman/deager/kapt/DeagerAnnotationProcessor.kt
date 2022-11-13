package se.ansman.deager.kapt

import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.service.AutoService
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.Processor
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion

@AutoService(Processor::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class DeagerAnnotationProcessor : BasicAnnotationProcessor() {
    override fun steps(): Iterable<Step> =
        listOf(EagerStep(processingEnv))
}