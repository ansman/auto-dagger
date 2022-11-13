package se.ansman.deager.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSType
import com.squareup.javapoet.ClassName
import se.ansman.deager.TypeLookup

class KspTypeLookup(private val resolver: Resolver) : TypeLookup<KSType> {
    private val cache = mutableMapOf<ClassName, KSType>()

    override fun getTypeForName(className: ClassName): KSType =
        cache.getOrPut(className) {
            val name = resolver.getKSNameFromString(className.canonicalName())
            val classDeclaration = requireNotNull(resolver.getClassDeclarationByName(name)) {
                "Could not find class with name $className"
            }
            classDeclaration.asStarProjectedType()
        }
}