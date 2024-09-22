package se.ansman.dagger.auto.ktorfit

import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import se.ansman.dagger.auto.compiler.AutoDaggerCompilationFactoryProvider
import se.ansman.dagger.auto.compiler.Errors
import se.ansman.dagger.auto.compiler.Compilation
import java.io.File

class KtorfitTest {
    @TempDir
    lateinit var tempDirectory: File

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `type must be an interface`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.ktorfit

                @AutoProvideService
                abstract class ApiService {
                    @de.jensklingenberg.ktorfit.http.GET("users")
                    suspend fun getUsers(): List<String>
                }
                """
            )
            .assertFailedWithMessage(Errors.Ktorfit.nonInterface)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `type must not be generic`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.ktorfit

                @AutoProvideService
                interface ApiService<T> {
                    @de.jensklingenberg.ktorfit.http.GET("users")
                    suspend fun getUsers(): List<String>
                }
                """
            )
            .assertFailedWithMessage(Errors.genericType(AutoProvideService::class))
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `type must not be private`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.ktorfit

                @AutoProvideService
                private interface ApiService {
                    @de.jensklingenberg.ktorfit.http.GET("users")
                    suspend fun getUsers(): List<String>
                }
                """
            )
            .assertFailedWithMessage(Errors.Ktorfit.privateType)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `type must have declared methods`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.ktorfit

                @AutoProvideService
                interface ApiService
                """
            )
            .assertFailedWithMessage(Errors.Ktorfit.emptyService)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `methods must be annotated`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.ktorfit

                @AutoProvideService
                interface ApiService {
                    suspend fun getUsers(): List<String>
                }
                """
            )
            .assertFailedWithMessage(Errors.Ktorfit.invalidServiceMethod)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `cannot have reusable and a scope`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.ktorfit

                @javax.inject.Singleton
                @dagger.Reusable
                @AutoProvideService
                interface ApiService {
                    @de.jensklingenberg.ktorfit.http.GET("users")
                    suspend fun getUsers(): List<String>
                }
                """
            )
            .assertFailedWithMessage(Errors.Ktorfit.scopeAndReusable)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `invalid scope`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.ktorfit

                @dagger.hilt.android.scopes.FragmentScoped
                @AutoProvideService
                interface ApiService {
                    suspend fun getUsers(): List<String>
                }
                """
            )
            .assertFailedWithMessage(Errors.Ktorfit.invalidScope("FragmentScoped", "SingletonComponent", "Singleton"))
    }
}