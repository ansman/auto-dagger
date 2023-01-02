package se.ansman.deager.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSType
import se.ansman.deager.TypeLookup
import kotlin.reflect.KClass

class KspTypeLookup(private val resolver: Resolver) : TypeLookup<KSType> {
    private val cache = mutableMapOf<KClass<*>, KSType>()

    override fun getTypeForClass(klass: KClass<*>): KSType =
        cache.getOrPut(klass) {
            val name = resolver.getKSNameFromString(requireNotNull(klass.qualifiedName) {
                "Cannot get type from anonymous or local class $klass"
            })
            val classDeclaration = requireNotNull(resolver.getClassDeclarationByName(name)) {
                "Could not find class with name $klass"
            }
            classDeclaration.asStarProjectedType()
        }
}