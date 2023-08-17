package se.ansman.dagger.auto.compiler

import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import se.ansman.dagger.auto.compiler.common.testutils.KspAutoDaggerCompilation
import se.ansman.dagger.auto.compiler.ksp.AutoDaggerSymbolProcessorProvider

@Execution(ExecutionMode.SAME_THREAD)
class KspTest : BaseTest(KspAutoDaggerCompilation.Factory(::AutoDaggerSymbolProcessorProvider))