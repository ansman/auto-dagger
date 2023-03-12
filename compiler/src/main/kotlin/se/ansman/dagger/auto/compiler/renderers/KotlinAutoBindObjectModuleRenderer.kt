package se.ansman.dagger.auto.compiler.renderers

import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.ksp.KotlinPoetRenderEngine

object KotlinAutoBindObjectModuleRenderer : AutoBindObjectModuleRenderer<KSNode, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, FileSpec>(
    KotlinPoetRenderEngine,
    HiltKotlinModuleBuilder.Factory
) {
    override fun provideObject(type: ClassName): CodeBlock = CodeBlock.of("return %T", type)
}