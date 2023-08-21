package se.ansman.dagger.auto.compiler.autobind.renderer

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.compiler.common.kapt.JavaPoetRenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltJavaModuleBuilder
import javax.lang.model.element.Element

object JavaAutoBindModuleModuleRenderer : AutoBindObjectModuleRenderer<Element, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, JavaFile>(
    JavaPoetRenderEngine,
    HiltJavaModuleBuilder.Factory
) {
    override fun provideObject(type: ClassName): CodeBlock = CodeBlock.of("return \$T.INSTANCE", type)
}