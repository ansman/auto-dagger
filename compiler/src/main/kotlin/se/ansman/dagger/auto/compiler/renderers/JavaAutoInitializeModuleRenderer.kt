package se.ansman.dagger.auto.compiler.renderers

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.Initializable
import se.ansman.dagger.auto.compiler.kapt.JavaPoetRenderEngine
import javax.lang.model.element.Element

object JavaAutoInitializeModuleRenderer :
    AutoInitializeModuleRenderer<Element, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, JavaFile>(
        JavaPoetRenderEngine,
        ::HiltJavaModuleBuilder
    ) {

    override fun createInitializableFromLazy(parameter: ParameterSpec, priority: Int?): CodeBlock =
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

    override fun wrapInitializable(parameter: ParameterSpec, priority: Int): CodeBlock =
        CodeBlock.of(
            "return \$T.withPriority(\$N, /* priority */ \$L)",
            Initializable::class.java,
            parameter,
            priority
        )
}