package se.ansman.deager.kapt

import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.auto.common.MoreTypes
import com.google.common.collect.HashMultimap
import com.google.common.collect.ImmutableSetMultimap
import com.google.common.collect.Multimap
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import se.ansman.deager.Eager
import se.ansman.deager.models.EagerObject
import se.ansman.deager.renderers.EagerObjectModuleJavaRenderer
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

@Suppress("UnstableApiUsage")
class EagerStep(
    private val processingEnv: ProcessingEnvironment
) : BasicAnnotationProcessor.Step {
    private val typeLookup = KaptTypeLookup(processingEnv.elementUtils)
    override fun annotations(): Set<String> = setOf(Eager::class.java.canonicalName)

    override fun process(elementsByAnnotation: ImmutableSetMultimap<String, Element>): Set<Element> {
        val eagerObjectsByModule: Multimap<ClassName, EagerProvider> = HashMultimap.create()
        for (element in elementsByAnnotation[Eager::class.java.canonicalName]) {
            try {
                when (element.kind) {
                    ElementKind.CLASS -> {
                        val type = MoreElements.asType(element)
                        val model = EagerObject.fromType(
                            type = type,
                            getAnnotations = { annotationMirrors.map(::KaptAnnotationModel) },
                            getParentType = { parentElement },
                            isTypePublic = { Modifier.PUBLIC in modifiers },
                            toClassName = ClassName::get,
                            simpleName = ClassName::simpleName,
                            implements = {
                                processingEnv.typeUtils.isAssignable(
                                    asType(),
                                    typeLookup.getTypeForClass(it)
                                )
                            },
                            error = ::KaptProcessingError
                        )
                        val renderer = EagerObjectModuleJavaRenderer(model)
                        val file = renderer.render {
                            addOriginatingElement(element)
                        }
                        file.writeTo(processingEnv.filer)
                    }
                    ElementKind.METHOD -> {
                        eagerObjectsByModule.put(
                            ClassName.get(MoreElements.asType(element.enclosingElement)),
                            EagerProvider(
                                eagerObject = EagerObject.fromMethod(
                                    method = MoreElements.asExecutable(element),
                                    getName = { simpleName.toString() },
                                    getReturnType = ExecutableElement::getReturnType,
                                    getReceiver = { null },
                                    getArguments = { parameters.asSequence().map(VariableElement::asType) },
                                    getEnclosingType = { MoreElements.asType(enclosingElement) },
                                    getDeclaration = MoreTypes::asTypeElement,
                                    getParentType =  { parentElement },
                                    isMethodPublic = { Modifier.PUBLIC in modifiers },
                                    isTypePublic = { Modifier.PUBLIC in modifiers },
                                    isCompanionObject = { false },
                                    toTypeName = TypeName::get,
                                    implements = {
                                        processingEnv.typeUtils.isAssignable(
                                            this,
                                            typeLookup.getTypeForClass(it)
                                        )
                                    },
                                    getAnnotations = { annotationMirrors.map(::KaptAnnotationModel) },
                                    getTypeAnnotations = { annotationMirrors.map(::KaptAnnotationModel) },
                                    error = ::KaptProcessingError
                                ),
                                originatingElement = element,
                            )
                        )
                    }
                    else -> {
                        throw KaptProcessingError("Unknown element kind ${element.kind}", element)
                    }
                }
            } catch (e: KaptProcessingError) {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "Deager: ${e.message}",
                    e.element,
                )
            }
        }

        for ((module, providers) in eagerObjectsByModule.asMap()) {
            val renderer = EagerObjectModuleJavaRenderer(module, providers.map { it.eagerObject })
            val file = renderer.render {
                for (provider in providers) {
                    addOriginatingElement(provider.originatingElement)
                }
            }
            file.writeTo(processingEnv.filer)
        }

        return emptySet()
    }

    private data class EagerProvider(
        val eagerObject: EagerObject<TypeName, AnnotationSpec>,
        val originatingElement: Element,
    )

}

@Suppress("UnstableApiUsage")
private val TypeElement.parentElement
    get() = enclosingElement
        .takeIf(MoreElements::isType)
        ?.let(MoreElements::asType)
