package se.ansman.dagger.auto.compiler.autobind.renderer

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.compiler.common.autobind.AutoBindObjectModuleRenderer
import se.ansman.dagger.auto.compiler.kapt.HiltJavaModuleBuilder
import se.ansman.dagger.auto.compiler.kapt.JavaPoetRenderEngine
import javax.lang.model.element.Element

object JavaAutoBindModuleModuleRenderer : AutoBindObjectModuleRenderer<Element, TypeName, ClassName, AnnotationSpec, ParameterSpec, MethodSpec.Builder, CodeBlock, JavaFile>(
    JavaPoetRenderEngine,
    HiltJavaModuleBuilder.Factory
) {
    override fun MethodSpec.Builder.provideObject(type: ClassName): CodeBlock = CodeBlock.of("return \$T.INSTANCE", type)
}