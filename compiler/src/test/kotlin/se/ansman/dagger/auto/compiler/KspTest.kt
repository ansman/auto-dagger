package se.ansman.dagger.auto.compiler

import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.SAME_THREAD)
class KspTest : BaseTest("ksp", ::KspAutoDaggerCompilation)