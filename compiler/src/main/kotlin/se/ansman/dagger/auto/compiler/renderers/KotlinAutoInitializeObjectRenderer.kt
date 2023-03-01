package se.ansman.dagger.auto.compiler.renderers

import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.Initializable
import se.ansman.dagger.auto.compiler.ksp.KotlinPoetRenderEngine

object KotlinAutoInitializeObjectRenderer :
    AutoInitializeModuleRenderer<KSNode, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, FileSpec>(
        KotlinPoetRenderEngine,
        ::HiltKotlinModuleBuilder
    ) {
    override fun createInitializableFromLazy(parameter: ParameterSpec, priority: Int?): CodeBlock =
        CodeBlock.of(
            "return %N.%M(%L)",
            parameter,
            Initializable.Companion::class.member("asInitializable"),
            if (priority == null) {
                CodeBlock.of("")
            } else {
                CodeBlock.of("priority = %L", priority)
            }
        )

    override fun wrapInitializable(parameter: ParameterSpec, priority: Int): CodeBlock =
        CodeBlock.of(
            "return %N.%M(%L)",
            parameter,
            Initializable.Companion::class.member("withPriority"),
            priority
        )
}