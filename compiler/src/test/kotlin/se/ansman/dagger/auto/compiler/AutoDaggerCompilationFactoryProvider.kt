package se.ansman.dagger.auto.compiler

import se.ansman.dagger.auto.compiler.common.testutils.CompilationFactoryProvider
import se.ansman.dagger.auto.compiler.kapt.AutoDaggerAnnotationProcessor
import se.ansman.dagger.auto.compiler.ksp.AutoDaggerSymbolProcessorProvider

class AutoDaggerCompilationFactoryProvider : CompilationFactoryProvider(
    ::AutoDaggerAnnotationProcessor,
    ::AutoDaggerSymbolProcessorProvider,
)