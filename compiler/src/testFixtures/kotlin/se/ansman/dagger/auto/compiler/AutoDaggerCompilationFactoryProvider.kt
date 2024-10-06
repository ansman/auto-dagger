package se.ansman.dagger.auto.compiler

import org.junit.jupiter.api.Named
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.stream.Stream
import kotlin.streams.asStream

class AutoDaggerCompilationFactoryProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> =
        namedFactories
            .asStream()
            .map(Arguments::of)

    companion object {
        private val namedFactories = sequenceOf(
            Named.of("KAPT", KaptCompilation.Factory),
            Named.of("KSP", KspCompilation.Factory),
            Named.of("CompilerPlugin", CompilerPluginCompilation.Factory),
        )

        val factories: Sequence<Compilation.Factory> = namedFactories.map { it.payload }
    }
}