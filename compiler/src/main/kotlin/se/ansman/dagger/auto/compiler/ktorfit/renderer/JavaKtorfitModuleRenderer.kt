package se.ansman.dagger.auto.compiler.ktorfit.renderer

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.compiler.common.ktorfit.renderer.KtorfitModuleRenderer
import se.ansman.dagger.auto.compiler.kapt.HiltJavaModuleBuilder
import se.ansman.dagger.auto.compiler.kapt.JavaPoetRenderEngine
import javax.lang.model.element.Element

object JavaKtorfitModuleRenderer :
    KtorfitModuleRenderer<Element, TypeName, ClassName, AnnotationSpec, ParameterSpec, MethodSpec.Builder, CodeBlock, JavaFile>(
        JavaPoetRenderEngine,
        HiltJavaModuleBuilder.Factory
    ) {

    override fun MethodSpec.Builder.provideService(serviceClass: ClassName, serviceFactoryParameter: ParameterSpec): CodeBlock =
        CodeBlock.of(
            "return \$T.create\$N(\$N)",
            ClassName.get(serviceClass.packageName(), "_${serviceClass.simpleName()}ImplKt"),
            serviceClass.simpleName(),
            serviceFactoryParameter
        )
}