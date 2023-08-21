package se.ansman.dagger.auto.compiler.common.ksp.processing

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.writeTo
import se.ansman.dagger.auto.compiler.common.Options
import se.ansman.dagger.auto.compiler.common.ksp.KotlinPoetRenderEngine
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerLogger
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine

class KspEnvironment(
    val environment: SymbolProcessorEnvironment,
) : AutoDaggerEnvironment<KSNode, TypeName, ClassName, AnnotationSpec, FileSpec>,
    RenderEngine<TypeName, ClassName, AnnotationSpec> by KotlinPoetRenderEngine {
    override val logger: AutoDaggerLogger<KSNode> = AutoDaggerKspLogger(
        logger = environment.logger,
        enableLogging = environment.options[Options.enableLogging]?.toBooleanStrict() ?: false
    )

    override fun write(file: FileSpec) {
        file.writeTo(environment.codeGenerator, aggregating = false)
    }
}