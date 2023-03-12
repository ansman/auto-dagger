package se.ansman.dagger.auto.compiler.kapt.processing

import com.google.auto.common.MoreElements
import com.google.common.collect.ImmutableSetMultimap
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import se.ansman.dagger.auto.compiler.TypeLookup
import se.ansman.dagger.auto.compiler.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.processing.ClassDeclaration
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import kotlin.reflect.KClass

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
            .read(
                KotlinClassHeader(
                    kind = metadata.kind,
                    metadataVersion = metadata.metadataVersion,
                    data1 = metadata.data1,
                    data2 = metadata.data2,
                    extraString = metadata.extraString,
                    packageName = metadata.packageName,
                    extraInt = metadata.extraInt,
                )
            )
            .let { it as? KotlinClassMetadata.Class }
            ?.toKmClass()
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
                    environment.logger.error("Unknown element kind ${element.kind}", element)
                    null
                }
            }
        }

    override fun lookupType(className: ClassName): ClassDeclaration<Element, TypeName, ClassName, AnnotationSpec> =
        typeLookup[className.canonicalName()]
}