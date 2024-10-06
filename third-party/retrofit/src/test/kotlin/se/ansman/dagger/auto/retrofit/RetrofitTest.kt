package se.ansman.dagger.auto.retrofit

import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import se.ansman.dagger.auto.compiler.AutoDaggerCompilationFactoryProvider
import se.ansman.dagger.auto.compiler.Errors
import se.ansman.dagger.auto.compiler.Compilation
import java.io.File

class RetrofitTest {
    @TempDir
    lateinit var tempDirectory: File

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `type must be an interface`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.retrofit

                @AutoProvideService
                abstract class ApiService {
                    @retrofit2.http.GET("users")
                    suspend fun getUsers(): List<String>
                }
                """
            )
            .assertFailedWithMessage(Errors.Retrofit.nonInterface)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `type must not be generic`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.retrofit

                @AutoProvideService
                interface ApiService<T> {
                    @retrofit2.http.GET("users")
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
                package se.ansman.dagger.auto.retrofit

                @AutoProvideService
                private interface ApiService {
                    @retrofit2.http.GET("users")
                    suspend fun getUsers(): List<String>
                }
                """
            )
            .assertFailedWithMessage(Errors.Retrofit.privateType)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `type must have declared methods`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.retrofit

                @AutoProvideService
                interface ApiService
                """
            )
            .assertFailedWithMessage(Errors.Retrofit.emptyService)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `methods must be annotated`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.retrofit

                @AutoProvideService
                interface ApiService {
                    suspend fun getUsers(): List<String>
                }
                """
            )
            .assertFailedWithMessage(Errors.Retrofit.invalidServiceMethod)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `cannot have reusable and a scope`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.retrofit

                @javax.inject.Singleton
                @dagger.Reusable
                @AutoProvideService
                interface ApiService {
                    @retrofit2.http.GET("users")
                    suspend fun getUsers(): List<String>
                }
                """
            )
            .assertFailedWithMessage(Errors.Retrofit.scopeAndReusable)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `invalid scope`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.retrofit

                @dagger.hilt.android.scopes.FragmentScoped
                @AutoProvideService
                interface ApiService {
                    suspend fun getUsers(): List<String>
                }
                """
            )
            .assertFailedWithMessage(Errors.Retrofit.invalidScope("FragmentScoped", "SingletonComponent", "Singleton"))
    }
}