package se.ansman.dagger.auto.compiler.kapt.processing

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.compiler.TypeLookup
import se.ansman.dagger.auto.compiler.kapt.JavaPoetRenderEngine
import se.ansman.dagger.auto.compiler.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.processing.RenderEngine
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.tools.Diagnostic

class KaptEnvironment(
    val environment: ProcessingEnvironment,
) : AutoDaggerEnvironment<Element, TypeName, ClassName, AnnotationSpec, JavaFile> {
    override val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec> get() = JavaPoetRenderEngine

    val typeLookup = TypeLookup(environment.elementUtils::getTypeElement)

    override fun logError(message: String, node: Element) {
        log(Diagnostic.Kind.ERROR, message, node)
    }

    override fun logWarning(message: String, node: Element) {
        log(Diagnostic.Kind.WARNING, message, node)
    }

    override fun logInfo(message: String, node: Element?) {
        log(Diagnostic.Kind.NOTE, message, node)
    }

    private fun log(kind: Diagnostic.Kind, message: String, node: Element?) {
        environment.messager.printMessage(kind, "Auto Dagger: $message", node)
    }

    override fun write(file: JavaFile) {
        file.writeTo(environment.filer)
    }
}