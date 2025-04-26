package se.ansman.dagger.auto.compiler

import se.ansman.dagger.auto.compiler.ksp.AutoDaggerSymbolProcessorProvider

class AutoDaggerCompilationFactoryProvider : CompilationFactoryProvider(
    ::AutoDaggerSymbolProcessorProvider,
)