package se.ansman.dagger.auto.compiler.kapt.processing

import com.google.auto.common.MoreElements
import com.google.common.collect.ImmutableSetMultimap
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import kotlinx.metadata.jvm.KotlinClassMetadata
import se.ansman.dagger.auto.compiler.common.TypeLookup
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

class KaptResolver(
    override val environment: KaptEnvironment,
    private val annotatedElements: ImmutableSetMultimap<String, Element>,
) : AutoDaggerResolver<Element, TypeName, ClassName, AnnotationSpec> {
    override val typeLookup = TypeLookup { className ->
        KaptClassDeclaration(environment.typeLookup[className], this)
    }

    val kmClassLookup = TypeLookup { className ->
        val typeElement = typeLookup[className].node
        val metadata = typeElement.getAnnotation(Metadata::class.java)
            ?: return@TypeLookup null

        KotlinClassMetadata
            .readLenient(metadata)
            .let { it as? KotlinClassMetadata.Class }
            ?.kmClass
    }

    @Suppress("UnstableApiUsage")
    override fun nodesAnnotatedWith(annotation: String): Sequence<KaptNode> =
        annotatedElements[annotation].asSequence().mapNotNull { element ->
            when {
                MoreElements.isType(element) ->
                    KaptClassDeclaration(MoreElements.asType(element), this)

                element.kind == ElementKind.METHOD || element.kind == ElementKind.CONSTRUCTOR ->
                    KaptFunction(MoreElements.asExecutable(element), this)

                else -> {
                    environment.logger.error("Unknown element kind ${element.kind}", element)
                    null
                }
            }
        }

    override fun lookupType(className: ClassName): ClassDeclaration<Element, TypeName, ClassName, AnnotationSpec> =
        typeLookup[className.canonicalName()]
}