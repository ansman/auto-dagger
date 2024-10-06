package se.ansman.dagger.auto.compiler.common.processing

import se.ansman.dagger.auto.compiler.common.processing.model.ClassDeclaration
import kotlin.reflect.KClass

interface AutoDaggerResolver<N, TypeName, ClassName : TypeName> {
    val environment: AutoDaggerEnvironment<N, TypeName, ClassName, *, *>
    fun lookupType(className: ClassName): ClassDeclaration<N, TypeName, ClassName>
    fun lookupType(className: String) = lookupType(environment.className(className))
    fun lookupType(className: KClass<*>) = lookupType(environment.className(className))
}