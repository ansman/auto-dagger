package se.ansman.deager.models

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import dagger.Binds
import se.ansman.deager.Eager
import se.ansman.deager.Errors
import se.ansman.deager.Initializable
import javax.inject.Qualifier
import javax.inject.Scope
import javax.inject.Singleton

data class EagerObject(
    val targetType: TypeName,
    val priority: Int?,
    val method: Method,
    val qualifiers: Set<AnnotationModel> = emptySet(),
) {
    companion object {
        internal val eager = ClassName.get(Eager::class.java)
        internal val singleton = ClassName.get(Singleton::class.java)
        internal val scope = ClassName.get(Scope::class.java)
        internal val binds = ClassName.get(Binds::class.java)
        internal val qualifier = ClassName.get(Qualifier::class.java)
        internal val initializable = ClassName.get(Initializable::class.java)

        internal inline fun <M : Any, T : Any> fromMethod(
            method: M,
            getName: M.() -> String,
            getReturnType: M.() -> T,
            getReceiver: M.() -> T?,
            getArguments: M.() -> Sequence<T>,
            toTypeName: T.() -> TypeName,
            implements: T.(ClassName) -> Boolean,
            getAnnotations: M.() -> Sequence<AnnotationModel>,
            getTypeAnnotations: T.() -> Sequence<AnnotationModel>,
            error: (String, M) -> Throwable,
        ): EagerObject {
            val returnType = method.getReturnType()
            val methodAnnotation = method.getAnnotations().toList()
            val annotations = methodAnnotation
                .plus(if (methodAnnotation.any { it.isOfType(binds) }) {
                    method.getReceiver()?.getTypeAnnotations()
                        ?: method.getArguments().firstOrNull()?.getTypeAnnotations()
                        ?: emptySequence()
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

        internal inline fun <E : Any> fromType(
            element: E,
            getAnnotations: E.() -> Sequence<AnnotationModel>,
            toClassName: E.() -> ClassName,
            implements: E.(ClassName) -> Boolean,
            error: (String, E) -> Throwable,
        ): EagerObject {
            val annotations = element.getAnnotations().toList()
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
                    Method.Binding.fromType(targetType)
                } else {
                    Method.Provider.fromType(targetType)
                }
            )
        }
    }

    sealed class Method {
        abstract val name: String

        data class Binding(override val name: String) : Method() {
            companion object {
                fun fromType(type: ClassName) = Binding("bind${type.simpleName()}AsInitializable")
            }
        }

        data class Provider(override val name: String) : Method() {
            companion object {
                fun fromType(type: ClassName) = Provider("provide${type.simpleName()}AsInitializable")
            }
        }
    }
}
