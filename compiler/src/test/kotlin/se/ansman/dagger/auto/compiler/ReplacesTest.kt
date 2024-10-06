package se.ansman.dagger.auto.compiler

import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import se.ansman.dagger.auto.android.testing.Replaces
import se.ansman.dagger.auto.compiler.common.Errors
import java.io.File

class ReplacesTest {
    @TempDir
    lateinit var tempDirectory: File

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `supertype must not be generic`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                interface Repository
                @se.ansman.dagger.auto.AutoBind
                class RealRepository : Repository
                @se.ansman.dagger.auto.android.testing.Replaces(RealRepository::class)
                class FakeRepository<T> : Repository
                """
            )
            .assertFailedWithMessage(Errors.genericType(Replaces::class))
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `replaced type is not AutoBind`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                interface Repository
                class RealRepository : Repository
                @se.ansman.dagger.auto.android.testing.Replaces(RealRepository::class)
                class FakeRepository : Repository
                """
            )
            .assertFailedWithMessage(Errors.Replaces.targetIsNotAutoBind("se.ansman.RealRepository"))
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `replacement doesn't implement supertype`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                interface Repository
 
                @se.ansman.dagger.auto.AutoBind
                class RealRepository : Repository

                @se.ansman.dagger.auto.android.testing.Replaces(RealRepository::class)
                class FakeRepository
                """
            )
            .assertFailedWithMessage(
                Errors.Replaces.missingBoundType(
                    "se.ansman.RealRepository",
                    "se.ansman.Repository",
                    "se.ansman.FakeRepository"
                )
            )
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `replacement has auto bind`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                interface Repository
 
                @se.ansman.dagger.auto.AutoBind
                class RealRepository : Repository

                @se.ansman.dagger.auto.android.testing.Replaces(RealRepository::class)
                @se.ansman.dagger.auto.AutoBind
                @se.ansman.dagger.auto.AutoBindIntoSet
                @se.ansman.dagger.auto.AutoBindIntoMap
                @dagger.multibindings.StringKey("SomeKey")
                class FakeRepository : Repository
                """
            )
            .assertFailedWithMessage(Errors.Replaces.isAutoBindOrInitialize)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `replacement has auto initialize`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                interface Repository
 
                @se.ansman.dagger.auto.AutoBind
                class RealRepository : Repository

                @se.ansman.dagger.auto.android.testing.Replaces(RealRepository::class)
                @se.ansman.dagger.auto.AutoInitialize
                class FakeRepository : Repository
                """
            )
            .assertFailedWithMessage(Errors.Replaces.isAutoBindOrInitialize)
    }
}