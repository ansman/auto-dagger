package se.ansman.dagger.auto.renderers

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.kapt.JavaPoetRenderEngine
import javax.lang.model.element.Element

object JavaAutoBindModuleRenderer : AutoBindModuleRenderer<Element, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, JavaFile>(
    JavaPoetRenderEngine,
    ::HiltJavaModuleBuilder
)