package se.ansman.dagger.auto.compiler.kapt.processing

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import kotlinx.metadata.jvm.KotlinClassMetadata
import se.ansman.dagger.auto.compiler.common.TypeLookup
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.kapt.processing.model.KaptClassDeclaration
import javax.lang.model.element.Element
import kotlin.reflect.KClass

class KaptResolver(override val environment: KaptEnvironment) : AutoDaggerResolver<Element, TypeName, ClassName> {
    private val typeLookup = TypeLookup { className: String ->
        KaptClassDeclaration(environment.typeLookup[className], this)
    }

    val kmClassLookup = TypeLookup { className: String ->
        val typeElement = typeLookup[className].node
        val metadata = typeElement.getAnnotation(Metadata::class.java)
            ?: return@TypeLookup null

        KotlinClassMetadata
            .readLenient(metadata)
            .let { it as? KotlinClassMetadata.Class }
            ?.kmClass
    }

    override fun lookupType(className: ClassName): KaptClassDeclaration =
        typeLookup[className.canonicalName()]

    override fun lookupType(className: String) = lookupType(environment.className(className))
    override fun lookupType(className: KClass<*>) = lookupType(environment.className(className))
}