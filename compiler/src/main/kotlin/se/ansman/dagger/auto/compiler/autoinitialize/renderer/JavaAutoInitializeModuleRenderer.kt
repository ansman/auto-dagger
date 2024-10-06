package se.ansman.dagger.auto.compiler.autoinitialize.renderer

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.Initializable
import se.ansman.dagger.auto.compiler.common.autoinitialize.renderer.AutoInitializeModuleRenderer
import se.ansman.dagger.auto.compiler.kapt.HiltJavaModuleBuilder
import se.ansman.dagger.auto.compiler.kapt.JavaPoetRenderEngine
import javax.lang.model.element.Element

object JavaAutoInitializeModuleRenderer :
    AutoInitializeModuleRenderer<Element, TypeName, ClassName, AnnotationSpec, ParameterSpec, MethodSpec.Builder, CodeBlock, JavaFile>(
        JavaPoetRenderEngine,
        HiltJavaModuleBuilder.Factory
    ) {

    override fun MethodSpec.Builder.createInitializableFromLazy(parameter: ParameterSpec, priority: Int?): CodeBlock =
        CodeBlock.of(
            "return \$T.fromLazy(\$N\$L)",
            Initializable::class.java,
            parameter,
            if (priority == null) {
                CodeBlock.of("")
            } else {
                CodeBlock.of(", /* priority */ \$L", priority)
            }
        )

    override fun MethodSpec.Builder.wrapInitializable(parameter: ParameterSpec, priority: Int): CodeBlock =
        CodeBlock.of(
            "return \$T.withPriority(\$N, /* priority */ \$L)",
            Initializable::class.java,
            parameter,
            priority
        )
}