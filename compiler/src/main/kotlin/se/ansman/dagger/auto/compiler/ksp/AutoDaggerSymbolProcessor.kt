package se.ansman.dagger.auto.compiler.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import se.ansman.dagger.auto.compiler.common.ksp.KspProcessor
import se.ansman.dagger.auto.compiler.common.ksp.processing.KspEnvironment
import se.ansman.dagger.auto.compiler.common.ksp.processing.KspResolver
import se.ansman.dagger.auto.compiler.processors.AutoBindProcessor
import se.ansman.dagger.auto.compiler.processors.AutoInitializeProcessor
import se.ansman.dagger.auto.compiler.processors.ReplacesProcessor
import se.ansman.dagger.auto.compiler.renderers.KotlinAutoBindObjectModuleRenderer
import se.ansman.dagger.auto.compiler.renderers.KotlinAutoInitializeObjectRenderer

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
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val kspResolver = KspResolver(environment, resolver)
        for (processor in processors) {
            processor.process(kspResolver)
        }
        return emptyList()
    }
}