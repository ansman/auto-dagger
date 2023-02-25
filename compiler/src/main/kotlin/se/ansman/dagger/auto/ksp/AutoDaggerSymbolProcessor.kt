package se.ansman.dagger.auto.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import se.ansman.dagger.auto.ksp.processing.KspEnvironment
import se.ansman.dagger.auto.ksp.processing.KspResolver
import se.ansman.dagger.auto.processors.AutoBindProcessor
import se.ansman.dagger.auto.processors.AutoInitializeProcessor
import se.ansman.dagger.auto.renderers.KotlinAutoBindModuleRenderer
import se.ansman.dagger.auto.renderers.KotlinAutoInitializeObjectRenderer

class AutoDaggerSymbolProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val environment = KspEnvironment(environment)
    private val processors: List<KspProcessor> = listOf(
        AutoInitializeProcessor(
            environment = this.environment,
            renderer = KotlinAutoInitializeObjectRenderer
        ),
        AutoBindProcessor(
            environment = this.environment,
            renderer = KotlinAutoBindModuleRenderer
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