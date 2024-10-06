package se.ansman.dagger.auto.compiler.optionallyprovided.renderer

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.compiler.common.optionallyprovided.renderer.OptionallyProvidedObjectModuleRenderer
import se.ansman.dagger.auto.compiler.kapt.HiltJavaModuleBuilder
import se.ansman.dagger.auto.compiler.kapt.JavaPoetRenderEngine
import javax.lang.model.element.Element

object JavaOptionallyProvidedObjectModuleRenderer :
    OptionallyProvidedObjectModuleRenderer<Element, TypeName, ClassName, AnnotationSpec, JavaFile>(
        JavaPoetRenderEngine,
        HiltJavaModuleBuilder.Factory
    )