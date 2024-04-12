package se.ansman.dagger.auto.compiler.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import se.ansman.dagger.auto.compiler.androidx.room.AndroidXRoomProcessor
import se.ansman.dagger.auto.compiler.androidx.room.renderer.KotlinAndroidXRoomDatabaseModuleRenderer
import se.ansman.dagger.auto.compiler.autobind.AutoBindProcessor
import se.ansman.dagger.auto.compiler.autobind.renderer.KotlinAutoBindObjectModuleRenderer
import se.ansman.dagger.auto.compiler.autoinitialize.AutoInitializeProcessor
import se.ansman.dagger.auto.compiler.autoinitialize.renderer.KotlinAutoInitializeObjectRenderer
import se.ansman.dagger.auto.compiler.common.ksp.KspProcessor
import se.ansman.dagger.auto.compiler.common.ksp.processing.KspEnvironment
import se.ansman.dagger.auto.compiler.common.ksp.processing.KspResolver
import se.ansman.dagger.auto.compiler.ktorfit.KtorfitProcessor
import se.ansman.dagger.auto.compiler.ktorfit.renderer.KotlinKtorfitObjectRenderer
import se.ansman.dagger.auto.compiler.replaces.ReplacesProcessor
import se.ansman.dagger.auto.compiler.retrofit.RetrofitProcessor
import se.ansman.dagger.auto.compiler.retrofit.renderer.KotlinRetrofitObjectRenderer

class AutoDaggerSymbolProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val environment = KspEnvironment(environment)
    private val processors: List<KspProcessor> = listOf(
        AutoInitializeProcessor(
            environment = this.environment,
            renderer = KotlinAutoInitializeObjectRenderer
        ),
        AutoBindProcessor(
            environment = this.environment,
            renderer = KotlinAutoBindObjectModuleRenderer
        ),
        ReplacesProcessor(
            environment = this.environment,
            renderer = KotlinAutoBindObjectModuleRenderer
        ),
        RetrofitProcessor(
            environment = this.environment,
            renderer = KotlinRetrofitObjectRenderer
        ),
        KtorfitProcessor(
            environment = this.environment,
            renderer = KotlinKtorfitObjectRenderer
        ),
        AndroidXRoomProcessor(
            environment = this.environment,
            renderer = KotlinAndroidXRoomDatabaseModuleRenderer
        ),
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val kspResolver = KspResolver(environment, resolver)
        for (processor in processors) {
            processor.process(kspResolver)
        }
        return emptyList()
    }
}