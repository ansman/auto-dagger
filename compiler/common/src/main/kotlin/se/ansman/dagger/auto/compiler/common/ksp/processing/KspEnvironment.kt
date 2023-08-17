package se.ansman.dagger.auto.compiler.common.ksp.processing

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.writeTo
import se.ansman.dagger.auto.compiler.common.ksp.KotlinPoetRenderEngine
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerLogger
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine

class KspEnvironment(
    val environment: SymbolProcessorEnvironment,
) : AutoDaggerEnvironment<KSNode, TypeName, ClassName, AnnotationSpec, FileSpec> {
    override val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec>
        get() = KotlinPoetRenderEngine

    override val logger: AutoDaggerLogger<KSNode> = AutoDaggerKspLogger(environment.logger)

    override fun write(file: FileSpec) {
        file.writeTo(environment.codeGenerator, aggregating = false)
    }
}