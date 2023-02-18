package se.ansman.dagger.auto.ksp

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSPropertyGetter
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Visibility
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import dagger.hilt.InstallIn
import dagger.hilt.codegen.OriginatingElement
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.AutoInitialize
import se.ansman.dagger.auto.Errors
import se.ansman.dagger.auto.models.AutoInitializeObject
import se.ansman.dagger.auto.renderers.KotlinAutoInitializeObject
import se.ansman.dagger.auto.renderers.KotlinAutoInitializeObjectRenderer
import java.io.BufferedWriter
import java.io.OutputStreamWriter

class AutoDaggerSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val typeLookup = KspTypeLookup(resolver)
        val objectsByModule = HashMultimap.create<ClassName, KotlinAutoInitializeObject>()
        val deferred = mutableListOf<KSAnnotated>()
        for (symbol in resolver.getSymbolsWithAnnotation(AutoInitialize::class.java.name)) {
            try {
                if (!processSymbol(resolver, typeLookup, symbol, objectsByModule)) {
                    deferred += symbol
                }
            } catch (e: KspProcessingError) {
                environment.logger.error("Auto Dagger: ${e.message}", e.symbol)
            }
        }
        for ((module, initializeObjects) in objectsByModule.asMap()) {
            val renderer = KotlinAutoInitializeObjectRenderer(module, initializeObjects)
            environment.codeGenerator.write(renderer.render { addHiltAnnotations(module) })
        }

        return deferred
    }

    private fun processSymbol(
        resolver: Resolver,
        typeLookup: KspTypeLookup,
        symbol: KSAnnotated,
        output: Multimap<ClassName, KotlinAutoInitializeObject>
    ): Boolean {
        return when (symbol) {
            is KSClassDeclaration -> {
                val model = AutoInitializeObject.fromType(
                    type = symbol,
                    getAnnotations = { annotations.map(::KspAnnotationModel).toList() },
                    getParentType = { parentDeclaration as? KSClassDeclaration },
                    isTypePublic = { getVisibility() == Visibility.PUBLIC },
                    toClassName = { toClassName() },
                    simpleName = ClassName::simpleName,
                    implements = { typeLookup.getTypeForClass(it).isAssignableFrom(asStarProjectedType()) },
                    error = ::KspProcessingError,
                )

                val renderer = KotlinAutoInitializeObjectRenderer(model)
                val file = renderer.render { addHiltAnnotations(model.targetType) }
                environment.codeGenerator.write(file)
                true
            }
            is KSPropertyGetter -> processSymbol(resolver, typeLookup, symbol.parent as KSPropertyDeclaration, output)
            is KSPropertyDeclaration -> {
                processMethod(
                    symbol = symbol,
                    name = "get${symbol.simpleName.asString().replaceFirstChar(Char::uppercaseChar)}",
                    parent = symbol.parentDeclaration,
                    returnType = symbol.getter?.returnType?.resolve() ?: return false,
                    visibility = symbol.getVisibility(),
                    receiver = { symbol.extensionReceiver?.resolve() },
                    arguments = { emptySequence() },
                    annotations = symbol.annotations + (symbol.getter?.annotations ?: emptySequence()),
                    typeLookup = typeLookup,
                    output = output
                )
                true
            }
            is KSFunctionDeclaration -> {
                processMethod(
                    symbol = symbol,
                    name = symbol.simpleName.asString(),
                    parent = symbol.parentDeclaration,
                    returnType = symbol.returnType?.resolve() ?: return false,
                    visibility = symbol.getVisibility(),
                    receiver = { symbol.extensionReceiver?.resolve() },
                    arguments = { symbol.parameters.asSequence().map { it.type.resolve() } },
                    annotations = symbol.annotations,
                    typeLookup = typeLookup,
                    output = output
                )
                true
            }
            else -> {
                environment.logger.error("Unknown symbol type ${symbol.javaClass}", symbol)
                true
            }
        }
    }

    private fun TypeSpec.Builder.addHiltAnnotations(targetType: ClassName) {
        addAnnotations(
            listOf(
                AnnotationSpec.builder(InstallIn::class)
                    .addMember("%T::class", SingletonComponent::class)
                    .build(),
                AnnotationSpec.builder(OriginatingElement::class)
                    .addMember("topLevelClass = %T::class", targetType.topLevelClassName())
                    .build()
            )
        )
    }

    private fun processMethod(
        symbol: KSAnnotated,
        name: String,
        parent: KSDeclaration?,
        returnType: KSType,
        visibility: Visibility,
        receiver: () -> KSType?,
        arguments: () -> Sequence<KSType>,
        annotations: Sequence<KSAnnotation>,
        typeLookup: KspTypeLookup,
        output: Multimap<ClassName, KotlinAutoInitializeObject>,
    ) {
        if (parent !is KSClassDeclaration) {
            throw KspProcessingError(Errors.methodInNonModule, symbol)
        }
        val model = AutoInitializeObject.fromMethod(
            method = symbol,
            getName = { name },
            getReturnType = { returnType },
            getReceiver = { receiver() },
            getArguments = { arguments() },
            getEnclosingType = { parent },
            getDeclaration = { declaration },
            getParentType = { parentDeclaration },
            isMethodPublic = { visibility == Visibility.PUBLIC },
            isTypePublic = { getVisibility() == Visibility.PUBLIC },
            isCompanionObject = { this is KSClassDeclaration && isCompanionObject },
            toTypeName = { toTypeName() },
            implements = { typeLookup.getTypeForClass(it).isAssignableFrom(this) },
            getAnnotations = { annotations.map(::KspAnnotationModel).toList() },
            getTypeAnnotations = { this.annotations.map(::KspAnnotationModel).toList() },
            error = ::KspProcessingError
        )
        output.put(parent.toClassName(), model)
    }

    private fun CodeGenerator.write(file: FileSpec) {
        this
            .createNewFile(
                dependencies = Dependencies(aggregating = false),
                packageName = file.packageName,
                fileName = file.name,
            )
            .let { OutputStreamWriter(it, Charsets.UTF_8) }
            .let(::BufferedWriter)
            .use(file::writeTo)
    }
}