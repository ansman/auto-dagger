package se.ansman.dagger.auto.ktorfit

import com.tschuchort.compiletesting.configureKsp
import de.jensklingenberg.ktorfit.KtorfitProcessorProvider
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import se.ansman.dagger.auto.compiler.AutoDaggerCompilationFactoryProvider
import se.ansman.dagger.auto.compiler.Compilation
import se.ansman.dagger.auto.compiler.KaptCompilation
import se.ansman.dagger.auto.compiler.common.Errors
import java.io.File

class KtorfitTest {
    @TempDir
    lateinit var tempDirectory: File

    private fun Compilation.Factory.compile(
        @Language("kotlin") vararg sources: String
    ) = create(tempDirectory).compile(*sources) {
        configureKsp(useKsp2 = true) {
            symbolProcessorProviders.add(KtorfitProcessorProvider())
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `type must have declared methods`(compilationFactory: Compilation.Factory) {
        if (compilationFactory == KaptCompilation.Factory) {
            return
        }
        compilationFactory
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
        compilationFactory
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
        compilationFactory
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
        compilationFactory
            .compile(
                """
                package se.ansman.dagger.auto.ktorfit

                @dagger.hilt.android.scopes.FragmentScoped
                @AutoProvideService
                interface ApiService {
                    @de.jensklingenberg.ktorfit.http.GET("users")
                    suspend fun getUsers(): List<String>
                }
                """
            )
            .assertFailedWithMessage(Errors.Ktorfit.invalidScope("FragmentScoped", "SingletonComponent", "Singleton"))
    }
}