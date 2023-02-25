package se.ansman.dagger.auto.kapt

import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.service.AutoService
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import se.ansman.dagger.auto.kapt.processing.KaptEnvironment
import se.ansman.dagger.auto.processors.AutoBindProcessor
import se.ansman.dagger.auto.processors.AutoInitializeProcessor
import se.ansman.dagger.auto.renderers.JavaAutoBindModuleRenderer
import se.ansman.dagger.auto.renderers.JavaAutoInitializeModuleRenderer
import javax.annotation.processing.Processor
import javax.lang.model.SourceVersion

@AutoService(Processor::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
class AutoDaggerAnnotationProcessor : BasicAnnotationProcessor() {
    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()
    override fun steps(): Iterable<Step> {
        val environment = KaptEnvironment(processingEnv)
        return listOf(
            AutoDaggerProcessorStep(environment, AutoInitializeProcessor(environment, JavaAutoInitializeModuleRenderer)),
            AutoDaggerProcessorStep(environment, AutoBindProcessor(environment, JavaAutoBindModuleRenderer)),
        )
    }
}
