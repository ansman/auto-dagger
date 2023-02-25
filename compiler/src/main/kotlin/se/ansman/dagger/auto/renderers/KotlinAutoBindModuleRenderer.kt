package se.ansman.dagger.auto.renderers

import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.ksp.KotlinPoetRenderEngine

object KotlinAutoBindModuleRenderer : AutoBindModuleRenderer<KSNode, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, FileSpec>(
    KotlinPoetRenderEngine,
    ::HiltKotlinModuleBuilder
)