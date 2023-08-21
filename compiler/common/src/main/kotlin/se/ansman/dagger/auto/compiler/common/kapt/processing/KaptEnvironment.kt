package se.ansman.dagger.auto.compiler.common.kapt.processing

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.compiler.common.Options
import se.ansman.dagger.auto.compiler.common.TypeLookup
import se.ansman.dagger.auto.compiler.common.kapt.JavaPoetRenderEngine
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerLogger
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class KaptEnvironment(
    val environment: ProcessingEnvironment,
) : AutoDaggerEnvironment<Element, TypeName, ClassName, AnnotationSpec, JavaFile>,
    RenderEngine<TypeName, ClassName, AnnotationSpec> by JavaPoetRenderEngine {
    override val logger: AutoDaggerLogger<Element> = AutoDaggerKaptLogger(
        messager = environment.messager,
        enableLogging = environment.options[Options.enableLogging]?.toBooleanStrict() ?: false
    )

    val typeLookup = TypeLookup(environment.elementUtils::getTypeElement)

    override fun write(file: JavaFile) {
        file.writeTo(environment.filer)
    }
}