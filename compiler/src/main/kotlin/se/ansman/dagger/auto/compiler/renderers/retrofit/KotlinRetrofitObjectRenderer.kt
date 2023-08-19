package se.ansman.dagger.auto.compiler.renderers.retrofit

import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.ksp.KotlinPoetRenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltKotlinModuleBuilder

object KotlinRetrofitObjectRenderer :
    RetrofitModuleRenderer<KSNode, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, FileSpec>(
        KotlinPoetRenderEngine,
        HiltKotlinModuleBuilder.Factory
    ) {

    override fun provideService(serviceClass: ClassName, retrofitParameter: ParameterSpec): CodeBlock =
        CodeBlock.of("return %N.%M()", retrofitParameter, MemberName("retrofit2", "create"))
}