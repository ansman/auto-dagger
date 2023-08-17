package se.ansman.dagger.auto.compiler.common.ksp.processing

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSPropertyGetter
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.TypeLookup
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration
import se.ansman.dagger.auto.compiler.common.processing.Node
import kotlin.reflect.KClass

class KspResolver(
    override val environment: KspEnvironment,
    val resolver: Resolver,
) : AutoDaggerResolver<KSDeclaration, TypeName, ClassName, AnnotationSpec> {
    override val typeLookup = TypeLookup { className ->
        resolver.getClassDeclarationByName(resolver.getKSNameFromString(className))
            ?.let { KspClassDeclaration(it, this) }
            ?: throw IllegalArgumentException("Could not find class for name $className")
    }

    override fun nodesAnnotatedWith(annotation: KClass<out Annotation>): Sequence<Node<KSDeclaration, TypeName, ClassName, AnnotationSpec>> =
        resolver.getSymbolsWithAnnotation(annotation.qualifiedName!!).mapNotNull { node ->
            when (node) {
                is KSClassDeclaration -> KspClassDeclaration(node, this)
                is KSFunctionDeclaration -> KspFunction(node, this)
                is KSPropertyGetter -> KspProperty(node.parent as KSPropertyDeclaration, this)
                is KSPropertyDeclaration -> KspProperty(node, this)
                else -> {
                    environment.logger.error("Unsupported type ${node.javaClass.name}", node)
                    null
                }
            }
        }

    override fun lookupType(className: ClassName): ClassDeclaration<KSDeclaration, TypeName, ClassName, AnnotationSpec> =
        typeLookup[className.canonicalName]
}