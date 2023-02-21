package se.ansman.dagger.auto.kapt.processing

import com.google.auto.common.MoreElements
import com.google.common.collect.ImmutableSetMultimap
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.TypeLookup
import se.ansman.dagger.auto.kapt.JavaPoetRenderEngine
import se.ansman.dagger.auto.processing.AutoDaggerProcessing
import se.ansman.dagger.auto.processing.RenderEngine
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.tools.Diagnostic
import kotlin.reflect.KClass

class KaptProcessing(
    val environment: ProcessingEnvironment,
    private val annotatedElements: ImmutableSetMultimap<String, Element>,
) : AutoDaggerProcessing<Element, TypeName, ClassName, AnnotationSpec, JavaFile> {
    override val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec> get() = JavaPoetRenderEngine
    val typeLookup = TypeLookup(environment.elementUtils::getTypeElement)

    @Suppress("UnstableApiUsage")
    override fun nodesAnnotatedWith(annotation: KClass<out Annotation>): Sequence<KaptNode> =
        annotatedElements[annotation.java.name].asSequence().mapNotNull { element ->
            when {
                MoreElements.isType(element) ->
                    KaptClassDeclaration(MoreElements.asType(element), this)

                element.kind == ElementKind.METHOD || element.kind == ElementKind.CONSTRUCTOR ->
                    KaptFunction(MoreElements.asExecutable(element), this)

                else -> {
                    logError("Unknown element kind ${element.kind}", element)
                    null
                }
            }
        }

    override fun logError(message: String, node: Element) {
        environment.messager.printMessage(Diagnostic.Kind.ERROR, "Auto Dagger: $message", node)
    }

    override fun write(file: JavaFile) {
        file.writeTo(environment.filer)
    }
}