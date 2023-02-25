package se.ansman.dagger.auto.ksp.processing

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSPropertyGetter
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.TypeLookup
import se.ansman.dagger.auto.processing.AutoDaggerResolver
import se.ansman.dagger.auto.processing.ClassDeclaration
import se.ansman.dagger.auto.processing.Node
import kotlin.reflect.KClass

class KspResolver(
    val environment: KspEnvironment,
    val resolver: Resolver,
) : AutoDaggerResolver<KSDeclaration, TypeName, ClassName, AnnotationSpec> {
    override val typeLookup = TypeLookup { className ->
        resolver.getClassDeclarationByName(resolver.getKSNameFromString(className))?.let {
            KspClassDeclaration(it, this)
        }
    }

    override fun nodesAnnotatedWith(annotation: KClass<out Annotation>): Sequence<Node<KSDeclaration, TypeName, ClassName, AnnotationSpec>> =
        resolver.getSymbolsWithAnnotation(annotation.qualifiedName!!).mapNotNull { node ->
            when (node) {
                is KSClassDeclaration -> KspClassDeclaration(node, this)
                is KSFunctionDeclaration -> KspFunction(node, this)
                is KSPropertyGetter -> KspProperty(node.parent as KSPropertyDeclaration, this)
                is KSPropertyDeclaration -> KspProperty(node, this)
                else -> {
                    environment.logError("Unsupported type ${node.javaClass.name}", node)
                    null
                }
            }
        }

    override fun lookupType(className: ClassName): ClassDeclaration<KSDeclaration, TypeName, ClassName, AnnotationSpec> =
        typeLookup[className.canonicalName]
}