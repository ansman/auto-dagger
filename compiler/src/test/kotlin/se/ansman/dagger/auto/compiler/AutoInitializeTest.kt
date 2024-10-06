package se.ansman.dagger.auto.compiler

import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import se.ansman.dagger.auto.AutoInitialize
import se.ansman.dagger.auto.compiler.common.Errors
import java.io.File

class AutoInitializeTest {
    @TempDir
    lateinit var tempDirectory: File

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `supertype must not be generic`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman
    
                @se.ansman.dagger.auto.AutoInitialize
                @javax.inject.Singleton
                class SomeThing<T>
                """
            )
            .assertFailedWithMessage(Errors.genericType(AutoInitialize::class))
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `the annotated class must be scoped`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                object Outer {
                  @se.ansman.dagger.auto.AutoInitialize(priority = 4711)
                  class SomeThing @javax.inject.Inject constructor()
                }
                """
            )
            .assertFailedWithMessage(Errors.AutoInitialize.unscopedType)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `the annotated class can only be scoped with @Singleton`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                @javax.inject.Scope
                annotation class SomeScope

                @se.ansman.dagger.auto.AutoInitialize
                @SomeScope
                class SomeThing @javax.inject.Inject constructor()
                """
            )
            .assertFailedWithMessage(Errors.AutoInitialize.wrongScope)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `the annotated method must be a provider or a binding`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                @dagger.Module
                object SomeModule {
                    @se.ansman.dagger.auto.AutoInitialize
                    @javax.inject.Singleton
                    fun provideString(): String = "string"
                }
                """
            )
            .assertFailedWithMessage(Errors.AutoInitialize.invalidAnnotatedMethod)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `the annotated method must not be top level`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                @dagger.Provides
                @se.ansman.dagger.auto.AutoInitialize
                @javax.inject.Singleton
                fun provideString(): String = "string"
                """
            )
            .assertFailedWithMessage(Errors.AutoInitialize.methodInNonModule)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `the annotated method must not be in a module`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman

                object SomeModule {
                    @dagger.Provides
                    @se.ansman.dagger.auto.AutoInitialize
                    @javax.inject.Singleton
                    fun provideString(): String = "string"
                }
                """
            )
            .assertFailedWithMessage(Errors.AutoInitialize.methodInNonModule)
    }
}