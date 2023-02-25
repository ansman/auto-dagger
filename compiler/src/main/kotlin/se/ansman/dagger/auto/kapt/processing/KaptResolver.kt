package se.ansman.dagger.auto.kapt.processing

import com.google.auto.common.MoreElements
import com.google.common.collect.ImmutableSetMultimap
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.TypeLookup
import se.ansman.dagger.auto.processing.AutoDaggerResolver
import se.ansman.dagger.auto.processing.ClassDeclaration
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import kotlin.reflect.KClass

class KaptResolver(
    val environment: KaptEnvironment,
    private val annotatedElements: ImmutableSetMultimap<String, Element>,
) : AutoDaggerResolver<Element, TypeName, ClassName, AnnotationSpec> {
    override val typeLookup = TypeLookup { className ->
        KaptClassDeclaration(environment.typeLookup[className], this)
    }

    @Suppress("UnstableApiUsage")
    override fun nodesAnnotatedWith(annotation: KClass<out Annotation>): Sequence<KaptNode> =
        annotatedElements[annotation.java.name].asSequence().mapNotNull { element ->
            when {
                MoreElements.isType(element) ->
                    KaptClassDeclaration(MoreElements.asType(element), this)

                element.kind == ElementKind.METHOD || element.kind == ElementKind.CONSTRUCTOR ->
                    KaptFunction(MoreElements.asExecutable(element), this)

                else -> {
                    environment.logError("Unknown element kind ${element.kind}", element)
                    null
                }
            }
        }

    override fun lookupType(className: ClassName): ClassDeclaration<Element, TypeName, ClassName, AnnotationSpec> =
        typeLookup[className.canonicalName()]
}