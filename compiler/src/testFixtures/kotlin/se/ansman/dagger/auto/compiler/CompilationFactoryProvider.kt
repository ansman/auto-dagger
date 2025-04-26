package se.ansman.dagger.auto.compiler

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.stream.Stream
import kotlin.streams.asStream

abstract class CompilationFactoryProvider(
    kspSymbolProcessorProvider: () -> SymbolProcessorProvider,
) : ArgumentsProvider {
    private val namedFactories = sequenceOf(
        Named.of("KSP", KspCompilation.Factory(kspSymbolProcessorProvider)),
    )

    val factories: Sequence<Compilation.Factory> = namedFactories.map { it.payload }

    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> =
        namedFactories
            .asStream()
            .map(Arguments::of)
}