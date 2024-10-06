package se.ansman.dagger.auto.compiler.ktorfit.renderer

import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.ktorfit.renderer.KtorfitModuleRenderer
import se.ansman.dagger.auto.compiler.ksp.HiltKotlinModuleBuilder
import se.ansman.dagger.auto.compiler.ksp.KotlinPoetRenderEngine

object KotlinKtorfitObjectRenderer :
    KtorfitModuleRenderer<KSNode, TypeName, ClassName, AnnotationSpec, ParameterSpec, FunSpec.Builder, CodeBlock, FileSpec>(
        KotlinPoetRenderEngine,
        HiltKotlinModuleBuilder.Factory
    ) {

    override fun FunSpec.Builder.provideService(serviceClass: ClassName, serviceFactoryParameter: ParameterSpec): CodeBlock =
        CodeBlock.of("return %N.create%N()", serviceFactoryParameter, serviceClass.simpleName)
}