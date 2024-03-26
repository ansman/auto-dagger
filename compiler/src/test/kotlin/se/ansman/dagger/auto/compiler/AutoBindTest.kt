package se.ansman.dagger.auto.compiler

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import se.ansman.dagger.auto.AutoBind
import se.ansman.dagger.auto.compiler.common.testutils.Compilation
import java.io.File

class AutoBindTest {
    @TempDir
    lateinit var tempDirectory: File
    
    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `supertype must not be generic`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman
    
                @se.ansman.dagger.auto.AutoBind
                class SomeThing<T> : Runnable {
                  override fun run(){}
                }
                """
            )
            .assertFailedWithMessage(Errors.genericType(AutoBind::class))
    }
    
    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `no supertypes`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman
    
                @se.ansman.dagger.auto.AutoBind
                class SomeThing
                """
            )
            .assertFailedWithMessage(Errors.AutoBind.noSuperTypes)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `binding initializable with auto initialize is prohibited`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                @se.ansman.dagger.auto.AutoBind
                @se.ansman.dagger.auto.AutoInitialize
                class SomeThing : se.ansman.dagger.auto.Initializable
                """
            )
            .assertFailedWithMessage(Errors.AutoBind.noSuperTypes)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `invalid asTypes`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman
    
                @se.ansman.dagger.auto.AutoBind(asTypes = [Runnable::class])
                class SomeThing
                """
            )
            .assertFailedWithMessage(Errors.AutoBind.missingBoundType("java.lang.Runnable"))
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `indirect supertype`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman
    
                @se.ansman.dagger.auto.AutoBind(asTypes = [AutoCloseable::class])
                class SomeThing : java.io.Closeable
                """
            )
            .assertFailedWithMessage(Errors.AutoBind.missingDirectSuperType("java.lang.AutoCloseable"))
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `missing binding key`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman
    
                @se.ansman.dagger.auto.AutoBindIntoMap
                class SomeThing : java.io.Closeable
                """
            )
            .assertFailedWithMessage(Errors.AutoBind.missingBindingKey)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `multiple supertypes`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman
    
                @se.ansman.dagger.auto.AutoBindIntoSet
                class SomeThing : java.io.Closeable, Runnable
                """
            )
            .assertFailedWithMessage(Errors.AutoBind.multipleSuperTypes)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `prevents binding into parent or unrelated components`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                interface Repository
 
                @se.ansman.dagger.auto.AutoBind(inComponent = dagger.hilt.components.SingletonComponent::class)
                @dagger.hilt.android.scopes.ActivityScoped
                class ActivityRepository @javax.inject.Inject constructor() : Repository
                """
            )
            .assertFailedWithMessage(Errors.AutoBind.parentComponent("SingletonComponent", "ActivityComponent"))
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                interface Repository
 
                @se.ansman.dagger.auto.AutoBind(inComponent = dagger.hilt.android.components.ActivityComponent::class)
                @dagger.hilt.android.scopes.FragmentScoped
                class FragmentRepository @javax.inject.Inject constructor() : Repository
                """
            )
            .assertFailedWithMessage(Errors.AutoBind.parentComponent("ActivityComponent", "FragmentComponent"))
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                interface Repository
 
                @se.ansman.dagger.auto.AutoBind(inComponent = dagger.hilt.components.SingletonComponent::class)
                @dagger.hilt.android.scopes.FragmentScoped
                class FragmentRepository @javax.inject.Inject constructor() : Repository
                """
            )
            .assertFailedWithMessage(Errors.AutoBind.parentComponent("SingletonComponent", "FragmentComponent"))
    }

    @Nested
    @DisplayName("auto bind generic as default")
    inner class AutoBindGenericAsDefault {

        @ParameterizedTest
        @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
        fun `target must be generic`(compilationFactory: Compilation.Factory) {
            compilationFactory.create(tempDirectory)
                .compile(
                    """
                    package se.ansman
        
                    @se.ansman.dagger.auto.BindGenericAs.Default(se.ansman.dagger.auto.BindGenericAs.Wildcard)
                    interface SomeThing
                    """
                )
                .assertFailedWithMessage(Errors.AutoBind.BindGenericAsDefault.nonGenericType)
        }

        @ParameterizedTest
        @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
        fun `cannot be applied to final classes`(compilationFactory: Compilation.Factory) {
            compilationFactory.create(tempDirectory)
                .compile(
                    """
                    package se.ansman
        
                    @se.ansman.dagger.auto.BindGenericAs.Default(se.ansman.dagger.auto.BindGenericAs.Wildcard)
                    class SomeThing<T>
                    """
                )
                .assertFailedWithMessage(Errors.AutoBind.BindGenericAsDefault.nonAbstractType)
        }

        @ParameterizedTest
        @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
        fun `can be applied to interfaces`(compilationFactory: Compilation.Factory) {
            compilationFactory.create(tempDirectory)
                .compile(
                    """
                    package se.ansman
        
                    @se.ansman.dagger.auto.BindGenericAs.Default(se.ansman.dagger.auto.BindGenericAs.Wildcard)
                    interface SomeThing<T>
                    """
                )
                .assertIsSuccessful()
        }

        @ParameterizedTest
        @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
        fun `can be applied to abstract classes`(compilationFactory: Compilation.Factory) {
            compilationFactory.create(tempDirectory)
                .compile(
                    """
                    package se.ansman
        
                    @se.ansman.dagger.auto.BindGenericAs.Default(se.ansman.dagger.auto.BindGenericAs.Wildcard)
                    abstract class SomeThing<T>
                    """
                )
                .assertIsSuccessful()
        }

        @ParameterizedTest
        @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
        fun `can be applied to sealed classes`(compilationFactory: Compilation.Factory) {
            compilationFactory.create(tempDirectory)
                .compile(
                    """
                package se.ansman
    
                @se.ansman.dagger.auto.BindGenericAs.Default(se.ansman.dagger.auto.BindGenericAs.Wildcard)
                sealed class SomeThing<T>
                """
                )
                .assertIsSuccessful()
        }

        @ParameterizedTest
        @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
        fun `cannot be applied to open classes`(compilationFactory: Compilation.Factory) {
            compilationFactory.create(tempDirectory)
                .compile(
                    """
                package se.ansman
    
                @se.ansman.dagger.auto.BindGenericAs.Default(se.ansman.dagger.auto.BindGenericAs.Wildcard)
                open class SomeThing<T>
                """
                )
                .assertFailedWithMessage(Errors.AutoBind.BindGenericAsDefault.nonAbstractType)
        }

        @ParameterizedTest
        @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
        fun `cannot be applied to objects`(compilationFactory: Compilation.Factory) {
            compilationFactory.create(tempDirectory)
                .compile(
                    """
                    package se.ansman
        
                    @se.ansman.dagger.auto.BindGenericAs.Default(se.ansman.dagger.auto.BindGenericAs.Wildcard)
                    object SomeThing
                    """
                )
                .assertFailedWithMessage(Errors.AutoBind.BindGenericAsDefault.nonAbstractType)
        }
    }
}