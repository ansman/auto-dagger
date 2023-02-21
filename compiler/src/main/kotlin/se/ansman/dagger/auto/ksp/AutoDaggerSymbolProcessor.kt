package se.ansman.dagger.auto.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import se.ansman.dagger.auto.ksp.processing.KspProcessing
import se.ansman.dagger.auto.processors.AutoInitializeProcessor
import se.ansman.dagger.auto.renderers.KotlinAutoInitializeObjectRenderer

class AutoDaggerSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val processing = KspProcessing(environment, resolver)
        val processor = AutoInitializeProcessor(
            processing = processing,
            renderer = KotlinAutoInitializeObjectRenderer
        )
        processor.process()
        return emptyList()
    }
}