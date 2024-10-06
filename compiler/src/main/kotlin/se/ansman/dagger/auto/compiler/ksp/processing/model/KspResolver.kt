package se.ansman.dagger.auto.compiler.ksp.processing.model

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.TypeLookup
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver

class KspResolver(
    override val environment: KspEnvironment,
    val resolver: Resolver,
) : AutoDaggerResolver<KSNode, TypeName, ClassName> {
    private val typeLookup = TypeLookup { className: String ->
        resolver.getClassDeclarationByName(resolver.getKSNameFromString(className))
            ?.let { KspClassDeclaration(it, this) }
            ?: throw IllegalArgumentException("Could not find class for name $className")
    }

    override fun lookupType(className: ClassName): KspClassDeclaration = typeLookup[className.canonicalName]
}