package se.ansman.deager.models

import dagger.Binds
import dagger.Module
import dagger.Provides
import se.ansman.deager.Eager
import se.ansman.deager.Errors
import se.ansman.deager.Initializable
import javax.inject.Qualifier
import javax.inject.Scope
import javax.inject.Singleton
import kotlin.reflect.KClass

data class EagerObject<out TypeName, out AnnotationSpec>(
    val targetType: TypeName,
    val priority: Int?,
    val isPublic: Boolean,
    val method: Method,
    val qualifiers: Set<AnnotationModel<AnnotationSpec>> = emptySet(),
) {
    companion object {
        internal val eager = Eager::class
        internal val singleton = Singleton::class
        private val scope = Scope::class
        internal val provides = Provides::class
        internal val binds = Binds::class
        private val qualifier = Qualifier::class
        private val module = Module::class
        internal val initializable = Initializable::class

        internal fun <Method : Any, Type : Any, TypeDeclaration : Any, TypeName, AnnotationSpec> fromMethod(
            method: Method,
            getName: Method.() -> String,
            getReturnType: Method.() -> Type,
            getReceiver: Method.() -> Type?,
            getArguments: Method.() -> Sequence<Type>,
            getEnclosingType: Method.() -> TypeDeclaration?,
            getDeclaration: Type.() -> TypeDeclaration,
            getParentType: TypeDeclaration.() -> TypeDeclaration?,
            isMethodPublic: Method.() -> Boolean,
            isTypePublic: TypeDeclaration.() -> Boolean,
            isCompanionObject: TypeDeclaration.() -> Boolean,
            toTypeName: Type.() -> TypeName,
            implements: Type.(KClass<*>) -> Boolean,
            getAnnotations: Method.() -> List<AnnotationModel<AnnotationSpec>>,
            getTypeAnnotations: TypeDeclaration.() -> List<AnnotationModel<AnnotationSpec>>,
            error: (String, Method) -> Throwable,
        ): EagerObject<TypeName, AnnotationSpec> {
            val returnType = method.getReturnType()
            val methodAnnotation = method.getAnnotations().toList()
            val isProvider = methodAnnotation.any { it.isOfType(provides) }
            val isBinding = methodAnnotation.any { it.isOfType(binds) }
            if (!isProvider && !isBinding) {
                throw error(Errors.invalidAnnotatedMethod, method)
            }

            val annotations = methodAnnotation
                .plus(
                    if (isBinding) {
                        method.getReceiver()?.getDeclaration()?.getTypeAnnotations()
                            ?: method.getArguments().firstOrNull()?.getDeclaration()?.getTypeAnnotations()
                            ?: emptyList()
                    } else {
                        returnType.getDeclaration().getTypeAnnotations()
                    }.toList()
                )
            val scopes = annotations.filter { a -> a.declaredAnnotations.any { it.isOfType(scope) } }
            val qualifiers = annotations.filterTo(mutableSetOf()) { a ->
                a.declaredAnnotations.any { it.isOfType(qualifier) }
            }
            if (scopes.isEmpty()) {
                throw error(Errors.unscopedType, method)
            }
            if (scopes.none { it.isOfType(singleton) }) {
                throw error(Errors.unscopedType, method)
            }
            val enclosingType = method.getEnclosingType()
                ?: throw error(Errors.methodInNonModule, method)

            val module = if (enclosingType.isCompanionObject()) {
                enclosingType.getParentType()
                    ?: throw error("Internal error! Could not find enclosing type for $enclosingType", method)
            } else {
                enclosingType
            }
            if (module.getTypeAnnotations().none { it.isOfType(EagerObject.module) }) {
                throw error(Errors.methodInNonModule, method)
            }

            fun TypeDeclaration.isPublic(): Boolean = isTypePublic() && (getParentType()?.isPublic() ?: true)

            val isInitializable = returnType.implements(initializable)
            val targetType = returnType.toTypeName()
            val name = "${method.getName()}AsInitializable"
            return EagerObject(
                targetType = targetType,
                priority = annotations.first { it.isOfType(eager) }.getValue("priority"),
                isPublic = method.isMethodPublic() && enclosingType.isPublic(),
                method = if (isInitializable) {
                    EagerObject.Method.Binding(name)
                } else {
                    EagerObject.Method.Provider(name)
                },
                qualifiers = qualifiers
            )
        }

        internal fun <T : Any, ClassName, AnnotationSpec> fromType(
            type: T,
            getAnnotations: T.() -> List<AnnotationModel<AnnotationSpec>>,
            getParentType: T.() -> T?,
            isTypePublic: T.() -> Boolean,
            toClassName: T.() -> ClassName,
            simpleName: ClassName.() -> String,
            implements: T.(KClass<*>) -> Boolean,
            error: (String, T) -> Throwable,
        ): EagerObject<ClassName, AnnotationSpec> {
            val annotations = type.getAnnotations()
            val scopes = annotations.filter { a -> a.declaredAnnotations.any { it.isOfType(scope) } }
            if (scopes.isEmpty()) {
                throw error(Errors.unscopedType, type)
            }
            if (scopes.none { it.isOfType(singleton) }) {
                throw error(Errors.unscopedType, type)
            }

            fun T.isPublic(): Boolean = isTypePublic() && (getParentType()?.isPublic() ?: true)

            val isInitializable = type.implements(initializable)
            val targetType = type.toClassName()
            return EagerObject(
                targetType = targetType,
                priority = annotations.first { it.isOfType(eager) }.getValue("priority"),
                isPublic = type.isPublic(),
                method = if (isInitializable) {
                    Method.Binding.fromType(targetType.simpleName())
                } else {
                    Method.Provider.fromType(targetType.simpleName())
                }
            )
        }
    }

    sealed class Method {
        abstract val name: String

        data class Binding(override val name: String) : Method() {
            companion object {
                fun fromType(className: String) = Binding("bind${className}AsInitializable")
            }
        }

        data class Provider(override val name: String) : Method() {
            companion object {
                fun fromType(className: String) = Provider("provide${className}AsInitializable")
            }
        }
    }
}
