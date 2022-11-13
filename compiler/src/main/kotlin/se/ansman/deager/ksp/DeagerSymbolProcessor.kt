package se.ansman.deager.ksp

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
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
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import dagger.Module
import se.ansman.deager.Eager
import se.ansman.deager.Errors
import se.ansman.deager.models.EagerObject
import se.ansman.deager.renderers.EagerObjectModuleRenderer
import java.io.BufferedWriter
import java.io.OutputStreamWriter

class DeagerSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val typeLookup = KspTypeLookup(resolver)
        val objectsByModule = HashMultimap.create<ClassName, EagerObject>()
        val deferred = mutableListOf<KSAnnotated>()
        for (symbol in resolver.getSymbolsWithAnnotation(Eager::class.java.name)) {
            try {
                if (!processSymbol(resolver, typeLookup, symbol, objectsByModule)) {
                    deferred += symbol
                }
            } catch (e: KspProcessingError) {
                environment.logger.error("Deager: ${e.message}", e.symbol)
            }
        }
        for ((module, eagerObjects) in objectsByModule.asMap()) {
            val renderer = EagerObjectModuleRenderer(module, eagerObjects)
            environment.codeGenerator.write(renderer.render())
        }

        return deferred
    }

    private fun processSymbol(
        resolver: Resolver,
        typeLookup: KspTypeLookup,
        symbol: KSAnnotated,
        output: Multimap<ClassName, EagerObject>
    ): Boolean {
        return when (symbol) {
            is KSClassDeclaration -> {
                val model = EagerObject.fromType(
                    element = symbol,
                    getAnnotations = { annotations.map { KspAnnotationModel(it, resolver) } },
                    toClassName = { toClassName(resolver) },
                    implements = { typeLookup.getTypeForName(it).isAssignableFrom(asStarProjectedType()) },
                    error = ::KspProcessingError,
                )

                val renderer = EagerObjectModuleRenderer(model)
                val file = renderer.render()
                environment.codeGenerator.write(file)
                true
            }
            is KSPropertyGetter -> processSymbol(resolver, typeLookup, symbol.parent as KSPropertyDeclaration, output)
            is KSPropertyDeclaration -> {
                processMethod(
                    symbol = symbol,
                    name = "get${symbol.simpleName.asString().replaceFirstChar(Char::uppercaseChar)}",
                    module = symbol.parentDeclaration,
                    returnType = symbol.getter?.returnType?.resolve() ?: return false,
                    receiver = { symbol.extensionReceiver?.resolve() },
                    arguments = { emptySequence() },
                    annotations = symbol.annotations + (symbol.getter?.annotations ?: emptySequence()),
                    resolver = resolver,
                    typeLookup = typeLookup,
                    output = output
                )
                true
            }
            is KSFunctionDeclaration -> {
                processMethod(
                    symbol = symbol,
                    name = symbol.simpleName.asString(),
                    module = symbol.parentDeclaration,
                    returnType = symbol.returnType?.resolve() ?: return false,
                    receiver = { symbol.extensionReceiver?.resolve() },
                    arguments = { symbol.parameters.asSequence().map { it.type.resolve() } },
                    annotations = symbol.annotations,
                    resolver = resolver,
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

    @OptIn(KspExperimental::class)
    private fun processMethod(
        symbol: KSAnnotated,
        name: String,
        module: KSDeclaration?,
        returnType: KSType,
        receiver: () -> KSType?,
        arguments: () -> Sequence<KSType>,
        annotations: Sequence<KSAnnotation>,
        resolver: Resolver,
        typeLookup: KspTypeLookup,
        output: Multimap<ClassName, EagerObject>,
    ) {
        if (module !is KSClassDeclaration) {
            throw KspProcessingError(Errors.methodInNonModule, symbol)
        }

        if (!module.isAnnotationPresent(Module::class)) {
            throw KspProcessingError(Errors.methodInNonModule, module)
        }

        val model = EagerObject.fromMethod(
            method = symbol,
            getName = { name },
            getReturnType = { returnType },
            getReceiver = { receiver() },
            getArguments = { arguments() },
            toTypeName = { toTypeName(resolver) },
            implements = { typeLookup.getTypeForName(it).isAssignableFrom(this) },
            getAnnotations = { annotations.map { KspAnnotationModel(it, resolver) } },
            getTypeAnnotations = { this.annotations.map { KspAnnotationModel(it, resolver) } },
            error = ::KspProcessingError
        )
        output.put(module.toClassName(resolver), model)
    }

    private fun CodeGenerator.write(file: JavaFile) {
        this
            .createNewFile(
                dependencies = Dependencies(aggregating = false),
                packageName = file.packageName,
                fileName = file.typeSpec.name,
                extensionName = "java"
            )
            .let { OutputStreamWriter(it, Charsets.UTF_8) }
            .let(::BufferedWriter)
            .use(file::writeTo)
    }
}