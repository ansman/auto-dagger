package se.ansman.dagger.auto.compiler

import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import se.ansman.dagger.auto.OptionallyProvided
import se.ansman.dagger.auto.compiler.common.Errors
import java.io.File

class OptionallyProvidedTest {
    @TempDir
    lateinit var tempDirectory: File
    
    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `type must not be generic`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman
    
                @se.ansman.dagger.auto.OptionallyProvided
                interface SomeThing<T>
                """
            )
            .assertFailedWithMessage(Errors.genericType(OptionallyProvided::class))
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `prevents binding into parent or unrelated components`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                @se.ansman.dagger.auto.OptionallyProvided(inComponent = dagger.hilt.components.SingletonComponent::class)
                @dagger.hilt.android.scopes.ActivityScoped
                interface Repository
                """
            )
            .assertFailedWithMessage(Errors.parentComponent("SingletonComponent", "ActivityComponent"))
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                @se.ansman.dagger.auto.OptionallyProvided(inComponent = dagger.hilt.android.components.ActivityComponent::class)
                @dagger.hilt.android.scopes.FragmentScoped
                interface Repository
                """
            )
            .assertFailedWithMessage(Errors.parentComponent("ActivityComponent", "FragmentComponent"))
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                @se.ansman.dagger.auto.OptionallyProvided(inComponent = dagger.hilt.components.SingletonComponent::class)
                @dagger.hilt.android.scopes.FragmentScoped
                interface Repository
                """
            )
            .assertFailedWithMessage(Errors.parentComponent("SingletonComponent", "FragmentComponent"))
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `cannot annotate objects`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                @se.ansman.dagger.auto.OptionallyProvided
                object Stuff
                """
            )
            .assertFailedWithMessage(Errors.OptionallyProvided.objectType)
    }
}