package se.ansman.dagger.auto.compiler.optionallyprovided.renderer

import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.optionallyprovided.renderer.OptionallyProvidedObjectModuleRenderer
import se.ansman.dagger.auto.compiler.ksp.HiltKotlinModuleBuilder
import se.ansman.dagger.auto.compiler.ksp.KotlinPoetRenderEngine

object KotlinOptionallyProvidedObjectModuleRenderer :
    OptionallyProvidedObjectModuleRenderer<KSNode, TypeName, ClassName, AnnotationSpec, FileSpec>(
        KotlinPoetRenderEngine,
        HiltKotlinModuleBuilder.Factory
    )