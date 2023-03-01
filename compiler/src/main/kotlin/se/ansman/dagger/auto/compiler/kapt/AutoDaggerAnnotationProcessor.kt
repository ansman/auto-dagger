package se.ansman.dagger.auto.compiler.kapt

import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.service.AutoService
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import se.ansman.dagger.auto.compiler.kapt.processing.AutoDaggerKaptLogger
import se.ansman.dagger.auto.compiler.kapt.processing.KaptEnvironment
import se.ansman.dagger.auto.compiler.processors.AutoBindProcessor
import se.ansman.dagger.auto.compiler.processors.AutoInitializeProcessor
import se.ansman.dagger.auto.compiler.processors.ReplacesProcessor
import se.ansman.dagger.auto.compiler.renderers.JavaAutoBindModuleModuleRenderer
import se.ansman.dagger.auto.compiler.renderers.JavaAutoInitializeModuleRenderer
import javax.annotation.processing.Processor
import javax.lang.model.SourceVersion

@AutoService(Processor::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
class AutoDaggerAnnotationProcessor : BasicAnnotationProcessor() {
    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()
    override fun getSupportedOptions(): Set<String> = setOf(AutoDaggerKaptLogger.enableLogging)
    override fun steps(): Iterable<Step> {
        val environment = KaptEnvironment(processingEnv)
        return listOf(
            AutoDaggerProcessorStep(environment, AutoInitializeProcessor(environment, JavaAutoInitializeModuleRenderer)),
            AutoDaggerProcessorStep(environment, AutoBindProcessor(environment, JavaAutoBindModuleModuleRenderer)),
            AutoDaggerProcessorStep(environment, ReplacesProcessor(environment, JavaAutoBindModuleModuleRenderer)),
        )
    }
}
