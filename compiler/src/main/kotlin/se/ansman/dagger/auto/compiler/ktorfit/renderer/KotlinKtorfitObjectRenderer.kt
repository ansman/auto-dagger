package se.ansman.dagger.auto.compiler.ktorfit.renderer

import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.ksp.KotlinPoetRenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltKotlinModuleBuilder

object KotlinKtorfitObjectRenderer :
    KtorfitModuleRenderer<KSDeclaration, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, FileSpec>(
        KotlinPoetRenderEngine,
        HiltKotlinModuleBuilder.Factory
    ) {

    override fun provideService(serviceClass: ClassName, serviceFactoryParameter: ParameterSpec): CodeBlock =
        CodeBlock.of("return %N.create()", serviceFactoryParameter)
}