package se.ansman.dagger.auto.compiler.plugin.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrDeclarationBase
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import se.ansman.dagger.auto.compiler.common.androidx.room.AndroidXRoomProcessor
import se.ansman.dagger.auto.compiler.common.autobind.AutoBindProcessor
import se.ansman.dagger.auto.compiler.common.autoinitialize.AutoInitializeProcessor
import se.ansman.dagger.auto.compiler.common.ktorfit.KtorfitProcessor
import se.ansman.dagger.auto.compiler.common.optionallyprovided.OptionallyProvidedProcessor
import se.ansman.dagger.auto.compiler.common.replaces.ReplacesProcessor
import se.ansman.dagger.auto.compiler.common.retrofit.RetrofitProcessor
import se.ansman.dagger.auto.compiler.plugin.androidx.room.renderer.KirAndroidXRoomDatabaseModuleRenderer
import se.ansman.dagger.auto.compiler.plugin.autobind.renderer.KirAutoBindObjectModuleRenderer
import se.ansman.dagger.auto.compiler.plugin.autoinitialize.renderer.KirAutoInitializeObjectRenderer
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirClassName
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirDeclaration
import se.ansman.dagger.auto.compiler.plugin.ir.model.toKirDeclaration
import se.ansman.dagger.auto.compiler.plugin.ktorfit.renderer.KirKtorfitObjectRenderer
import se.ansman.dagger.auto.compiler.plugin.optionallyprovided.renderer.KirOptionallyProvidedObjectModuleRenderer
import se.ansman.dagger.auto.compiler.plugin.retrofit.renderer.KirRetrofitObjectRenderer
import java.io.File


class AutoDaggerIrGenerationExtension(
    private val messageCollector: MessageCollector,
    private val enableLogging: Boolean,
    private val dumpFilesTo: File?,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val environment = KirEnvironment(
            logger = AutoDaggerKirLogger(messageCollector, enableLogging),
            context = pluginContext,
            moduleFragment = moduleFragment,
            dumpFilesTo = dumpFilesTo,
        )
        val autoBindRenderer = KirAutoBindObjectModuleRenderer(environment)
        val processors = listOf(
            AutoInitializeProcessor(environment, KirAutoInitializeObjectRenderer(environment)),
            AutoBindProcessor(environment, autoBindRenderer),
            ReplacesProcessor(environment, autoBindRenderer),
            RetrofitProcessor(environment, KirRetrofitObjectRenderer(environment)),
            KtorfitProcessor(environment, KirKtorfitObjectRenderer(environment)),
            AndroidXRoomProcessor(environment, KirAndroidXRoomDatabaseModuleRenderer(environment)),
            OptionallyProvidedProcessor(environment, KirOptionallyProvidedObjectModuleRenderer(environment)),
        )
        val annotations = processors
            .asSequence()
            .flatMap { it.annotations }
            .distinct()
            .map { KirClassName.bestGuess(it).classId }
            .toSet()

        val classes = mutableMapOf<ClassId, MutableList<KirDeclaration>>()
        moduleFragment.accept(ClassVisitor(environment, classes), annotations)
        val elements = classes
            .mapKeys { (key, _) -> key.asFqNameString() }
            .withDefault { emptyList() }

        for (processor in processors) {
            processor.process(elements, environment)
        }
    }
}

private class ClassVisitor(
    private val environment: KirEnvironment,
    private val output: MutableMap<ClassId, MutableList<KirDeclaration>>
) : IrElementVisitor<Unit, Set<ClassId>> {
    private val dumpContents = FqName(DumpContents::class.java.name)
    override fun visitElement(element: IrElement, data: Set<ClassId>) {
        element.acceptChildren(this, data)
    }

    override fun visitDeclaration(declaration: IrDeclarationBase, data: Set<ClassId>) {
        super.visitDeclaration(declaration, data)
        var kirDeclaration: KirDeclaration? = null
        for (annotation in data) {
            if (declaration.hasAnnotation(annotation)) {
                output.getOrPut(annotation) { mutableListOf() }
                    .add(
                        kirDeclaration
                            ?: declaration.toKirDeclaration(environment)
                                ?.also { kirDeclaration = it }
                            ?: return
                    )
            }
        }
        if (declaration.hasAnnotation(dumpContents)) {
            environment.logger.warning(declaration.dump(), declaration)
            environment.logger.warning(declaration.dumpSrc(), declaration)
        }
    }

    override fun visitBody(body: IrBody, data: Set<ClassId>) {}
}