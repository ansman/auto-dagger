package se.ansman.dagger.auto.compiler

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import dagger.hilt.processor.internal.root.ComponentTreeDepsProcessor
import dagger.internal.codegen.ComponentProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.OutputStream
import kotlin.test.assertEquals

@OptIn(ExperimentalCompilerApi::class)
class HiltRootValidationTest {
    @TempDir
    lateinit var tempDirectory: File

    @Test
    fun `generated AutoBind module with default constructor passes Hilt root module validation`() {
        val result = KotlinCompilation()
            .apply {
                workingDir = tempDirectory
                inheritClassPath = true
                messageOutputStream = OutputStream.nullOutputStream()
                sources = hiltRootSources() + generatedAutoBindModule()
                annotationProcessors = listOf(ComponentTreeDepsProcessor(), ComponentProcessor())
                kaptArgs = mutableMapOf("dagger.hilt.internal.useAggregatingRootProcessor" to "true")
            }
            .compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
    }

    private fun hiltRootSources(): List<SourceFile> =
        listOf(
            SourceFile.kotlin(
                name = "Application.kt",
                contents = """
                package android.app

                open class Application
                """
            ),
            SourceFile.kotlin(
                name = "HiltAndroidApp.kt",
                contents = """
                package dagger.hilt.android

                import kotlin.reflect.KClass

                @Target(AnnotationTarget.CLASS)
                @Retention(AnnotationRetention.RUNTIME)
                annotation class HiltAndroidApp(val value: KClass<*> = Void::class)
                """
            ),
            SourceFile.kotlin(
                name = "AggregatedRoot.kt",
                contents = """
                package dagger.hilt.internal.aggregatedroot

                import kotlin.reflect.KClass

                @Target(AnnotationTarget.CLASS)
                @Retention(AnnotationRetention.BINARY)
                annotation class AggregatedRoot(
                    val root: String,
                    val rootPackage: String,
                    val rootSimpleNames: Array<String>,
                    val originatingRoot: String,
                    val originatingRootPackage: String,
                    val originatingRootSimpleNames: Array<String>,
                    val rootAnnotation: KClass<*>,
                    val rootComponentPackage: String = "",
                    val rootComponentSimpleNames: Array<String> = [],
                )
                """
            ),
            SourceFile.kotlin(
                name = "ComponentTreeDeps.kt",
                contents = """
                package dagger.hilt.internal.componenttreedeps

                import kotlin.reflect.KClass

                @Target(AnnotationTarget.CLASS)
                @Retention(AnnotationRetention.BINARY)
                annotation class ComponentTreeDeps(
                    val rootDeps: Array<KClass<*>> = [],
                    val defineComponentDeps: Array<KClass<*>> = [],
                    val aliasOfDeps: Array<KClass<*>> = [],
                    val aggregatedDeps: Array<KClass<*>> = [],
                    val uninstallModulesDeps: Array<KClass<*>> = [],
                    val earlyEntryPointDeps: Array<KClass<*>> = [],
                )
                """
            ),
            SourceFile.kotlin(
                name = "TestApplication.kt",
                contents = """
                package se.ansman

                @dagger.hilt.android.HiltAndroidApp(Hilt_TestApplication::class)
                class TestApplication : Hilt_TestApplication()

                open class Hilt_TestApplication : android.app.Application()

                interface RecordHandler<Result, Command>

                class CreateRecord {
                    class Result
                }

                class CreateRecordHandler @javax.inject.Inject constructor() :
                    RecordHandler<CreateRecord.Result, CreateRecord>
                """
            ),
            SourceFile.java(
                name = "AggregatedRootMetadata.java",
                contents = """
                package dagger.hilt.internal.aggregatedroot;

                @AggregatedRoot(
                    root = "se.ansman.TestApplication",
                    rootPackage = "se.ansman",
                    rootSimpleNames = "TestApplication",
                    originatingRoot = "se.ansman.TestApplication",
                    originatingRootPackage = "se.ansman",
                    originatingRootSimpleNames = "TestApplication",
                    rootAnnotation = dagger.hilt.android.HiltAndroidApp.class,
                    rootComponentPackage = "dagger.hilt.components",
                    rootComponentSimpleNames = "SingletonComponent"
                )
                public final class AggregatedRootMetadata {}
                """
            ),
            SourceFile.java(
                name = "AggregatedDepsMetadata.java",
                contents = """
                package hilt_aggregated_deps;

                @dagger.hilt.processor.internal.aggregateddeps.AggregatedDeps(
                    components = "dagger.hilt.components.SingletonComponent",
                    modules = "se.ansman.AutoBindCreateRecordHandlerSingletonModule"
                )
                public final class AggregatedDepsMetadata {}
                """
            ),
            SourceFile.java(
                name = "TestApplication_ComponentTreeDeps.java",
                contents = """
                package se.ansman;

                @dagger.hilt.internal.componenttreedeps.ComponentTreeDeps(
                    rootDeps = dagger.hilt.internal.aggregatedroot.AggregatedRootMetadata.class,
                    defineComponentDeps =
                        dagger.hilt.processor.internal.definecomponent.codegen._dagger_hilt_components_SingletonComponent.class,
                    aggregatedDeps = hilt_aggregated_deps.AggregatedDepsMetadata.class
                )
                public final class TestApplication_ComponentTreeDeps {}
                """
            )
        )

    private fun generatedAutoBindModule(): SourceFile =
        SourceFile.kotlin(
            name = "AutoBindCreateRecordHandlerSingletonModule.kt",
            contents = """
            // Code generated by Auto Dagger. Do not edit.
            package se.ansman

            import dagger.Binds
            import dagger.Module
            import dagger.hilt.InstallIn
            import dagger.hilt.codegen.OriginatingElement
            import dagger.hilt.components.SingletonComponent
            import dagger.multibindings.ClassKey
            import dagger.multibindings.IntoMap
            import javax.annotation.processing.Generated

            @Generated("se.ansman.dagger.auto.compiler.autobind.AutoBindProcessor")
            @Module
            @InstallIn(SingletonComponent::class)
            @OriginatingElement(topLevelClass = CreateRecordHandler::class)
            public abstract class AutoBindCreateRecordHandlerSingletonModule {
                @Binds
                @IntoMap
                @ClassKey(CreateRecord::class)
                public abstract fun bindCreateRecordHandlerAsRecordHandlerIntoMap(
                    createRecordHandler: CreateRecordHandler
                ): RecordHandler<CreateRecord.Result, CreateRecord>
            }
            """
        )
}
