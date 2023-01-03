package se.ansman.deager.models

import dagger.Binds
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
    val method: Method,
    val qualifiers: Set<AnnotationModel<AnnotationSpec>> = emptySet(),
) {
    companion object {
        internal val eager = Eager::class
        internal val singleton = Singleton::class
        internal val scope = Scope::class
        internal val binds = Binds::class
        internal val qualifier = Qualifier::class
        internal val initializable = Initializable::class

        internal inline fun <M : Any, T : Any, TypeName, AnnotationSpec> fromMethod(
            method: M,
            getName: M.() -> String,
            getReturnType: M.() -> T,
            getReceiver: M.() -> T?,
            getArguments: M.() -> Sequence<T>,
            toTypeName: T.() -> TypeName,
            implements: T.(KClass<*>) -> Boolean,
            getAnnotations: M.() -> List<AnnotationModel<AnnotationSpec>>,
            getTypeAnnotations: T.() -> List<AnnotationModel<AnnotationSpec>>,
            error: (String, M) -> Throwable,
        ): EagerObject<TypeName, AnnotationSpec> {
            val returnType = method.getReturnType()
            val methodAnnotation = method.getAnnotations().toList()
            val annotations = methodAnnotation
                .plus(if (methodAnnotation.any { it.isOfType(binds) }) {
                    method.getReceiver()?.getTypeAnnotations()
                        ?: method.getArguments().firstOrNull()?.getTypeAnnotations()
                        ?: emptyList()
                } else {
                    returnType.getTypeAnnotations()
                }.toList())
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

            val isInitializable = returnType.implements(initializable)
            val targetType = returnType.toTypeName()
            val name = "${method.getName()}AsInitializable"
            return EagerObject(
                targetType = targetType,
                priority = annotations.first { it.isOfType(eager) }.getValue("priority"),
                method = if (isInitializable) {
                    Method.Binding(name)
                } else {
                    Method.Provider(name)
                },
                qualifiers = qualifiers
            )
        }

        internal inline fun <E : Any, ClassName, AnnotationSpec> fromType(
            element: E,
            getAnnotations: E.() -> List<AnnotationModel<AnnotationSpec>>,
            toClassName: E.() -> ClassName,
            simpleName: ClassName.() -> String,
            implements: E.(KClass<*>) -> Boolean,
            error: (String, E) -> Throwable,
        ): EagerObject<ClassName, AnnotationSpec> {
            val annotations = element.getAnnotations()
            val scopes = annotations.filter { a -> a.declaredAnnotations.any { it.isOfType(scope) } }
            if (scopes.isEmpty()) {
                throw error(Errors.unscopedType, element)
            }
            if (scopes.none { it.isOfType(singleton) }) {
                throw error(Errors.unscopedType, element)
            }

            val isInitializable = element.implements(initializable)
            val targetType = element.toClassName()
            return EagerObject(
                targetType = targetType,
                priority = annotations.first { it.isOfType(eager) }.getValue("priority"),
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
