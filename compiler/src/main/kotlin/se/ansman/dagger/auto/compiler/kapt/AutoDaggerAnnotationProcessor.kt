package se.ansman.dagger.auto.compiler.kapt

import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.service.AutoService
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import se.ansman.dagger.auto.compiler.androidx.room.renderer.JavaAndroidXRoomDatabaseModuleRenderer
import se.ansman.dagger.auto.compiler.autobind.renderer.JavaAutoBindModuleModuleRenderer
import se.ansman.dagger.auto.compiler.autoinitialize.renderer.JavaAutoInitializeModuleRenderer
import se.ansman.dagger.auto.compiler.common.Options
import se.ansman.dagger.auto.compiler.common.androidx.room.AndroidXRoomProcessor
import se.ansman.dagger.auto.compiler.common.autobind.AutoBindProcessor
import se.ansman.dagger.auto.compiler.common.autoinitialize.AutoInitializeProcessor
import se.ansman.dagger.auto.compiler.common.ktorfit.KtorfitProcessor
import se.ansman.dagger.auto.compiler.common.optionallyprovided.OptionallyProvidedProcessor
import se.ansman.dagger.auto.compiler.common.replaces.ReplacesProcessor
import se.ansman.dagger.auto.compiler.common.retrofit.RetrofitProcessor
import se.ansman.dagger.auto.compiler.kapt.processing.KaptEnvironment
import se.ansman.dagger.auto.compiler.kapt.processing.KaptResolver
import se.ansman.dagger.auto.compiler.ktorfit.renderer.JavaKtorfitModuleRenderer
import se.ansman.dagger.auto.compiler.optionallyprovided.renderer.JavaOptionallyProvidedObjectModuleRenderer
import se.ansman.dagger.auto.compiler.retrofit.renderer.JavaRetrofitModuleRenderer
import javax.annotation.processing.Processor
import javax.lang.model.SourceVersion

@AutoService(Processor::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
class AutoDaggerAnnotationProcessor : BasicAnnotationProcessor() {
    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()
    override fun getSupportedOptions(): Set<String> = setOf(Options.enableLogging)
    override fun steps(): Iterable<Step> {
        val environment = KaptEnvironment(processingEnv)
        val resolver = KaptResolver(environment)
        return listOf(
            AutoDaggerProcessorStep(environment, resolver, AutoInitializeProcessor(environment, JavaAutoInitializeModuleRenderer)),
            AutoDaggerProcessorStep(environment, resolver, AutoBindProcessor(environment, JavaAutoBindModuleModuleRenderer)),
            AutoDaggerProcessorStep(environment, resolver, ReplacesProcessor(environment, JavaAutoBindModuleModuleRenderer)),
            AutoDaggerProcessorStep(environment, resolver, RetrofitProcessor(environment, JavaRetrofitModuleRenderer)),
            AutoDaggerProcessorStep(environment, resolver, KtorfitProcessor(environment, JavaKtorfitModuleRenderer)),
            AutoDaggerProcessorStep(environment, resolver, AndroidXRoomProcessor(environment, JavaAndroidXRoomDatabaseModuleRenderer)),
            AutoDaggerProcessorStep(environment, resolver, OptionallyProvidedProcessor(environment, JavaOptionallyProvidedObjectModuleRenderer)),
        )
    }
}
