package se.ansman.dagger.auto.ksp.processing

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSPropertyGetter
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.writeTo
import se.ansman.dagger.auto.TypeLookup
import se.ansman.dagger.auto.ksp.KotlinPoetRenderEngine
import se.ansman.dagger.auto.processing.AutoDaggerProcessing
import se.ansman.dagger.auto.processing.Node
import se.ansman.dagger.auto.processing.RenderEngine
import kotlin.reflect.KClass

class KspProcessing(
    val environment: SymbolProcessorEnvironment,
    val resolver: Resolver,
) : AutoDaggerProcessing<KSDeclaration, TypeName, ClassName, AnnotationSpec, FileSpec> {
    val typeLookup = TypeLookup {
        resolver.getClassDeclarationByName(resolver.getKSNameFromString(it))
    }

    override val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec>
        get() = KotlinPoetRenderEngine

    override fun nodesAnnotatedWith(annotation: KClass<out Annotation>): Sequence<Node<KSDeclaration, TypeName, ClassName, AnnotationSpec>> =
        resolver.getSymbolsWithAnnotation(annotation.qualifiedName!!).mapNotNull { node ->
            when (node) {
                is KSClassDeclaration -> KspClassDeclaration(node, this)
                is KSFunctionDeclaration -> KspFunction(node, this)
                is KSPropertyGetter -> KspProperty(node.parent as KSPropertyDeclaration, this)
                is KSPropertyDeclaration -> KspProperty(node, this)
                else -> {
                    logError("Unsupported type ${node.javaClass.name}", node)
                    null
                }
            }
        }

    override fun logError(message: String, node: KSDeclaration) = logError(message, node as KSNode)

    fun logError(message: String, node: KSNode) {
        environment.logger.error("Auto Dagger: $message", node)
    }

    override fun write(file: FileSpec) {
        file.writeTo(environment.codeGenerator, aggregating = false)
    }
}