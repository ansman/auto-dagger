package se.ansman.dagger.auto.compiler

import kotlin.reflect.KClass

object Errors {
    fun genericType(annotation: KClass<out Annotation>) =
        "Objects annotated with @${annotation.simpleName} cannot be generic"

    object AutoInitialize {
        const val wrongScope = "Objects annotated with @AutoInitialize must also be annotated with @Singleton"
        const val unscopedType = wrongScope
        const val methodInNonModule =
            "@AutoInitialize annotated methods must be declared inside a @Module annotated class, object or interface."
        const val invalidAnnotatedMethod = "Annotated methods must have either the @Provides or @Binds annotation"
    }

    object AutoBind {
        const val missingBindingKey = "To use @AutoBindIntoMap you must also annotate the type with a map key"
        const val multipleBindingKeys = "Multiple map keys specified, make sure there is only a single map key"
        const val noSuperTypes = "Multiple map keys specified, make sure there is only a single map key"
        const val multipleSuperTypes = "Multiple supertypes found. Use the `asTypes` parameter to specify which types to bind"

        fun missingBoundType(boundType: String): String =
            "$boundType is specified using `asTypes` but isn't a supertype"

        fun missingDirectSuperType(boundType: String): String =
            "$boundType is specified using `asTypes` but isn't a direct supertype"

        fun invalidComponent(component: String): String =
            "The specified component $component isn't a Dagger component"

        fun nonStandardScope(scope: String): String =
            "Scope $scope is not a standard scope so you must specify a component explicitly using the `inComponent` parameter."
    }

    object Replaces {
        const val isAutoBindOrInitialize = "Objects annotated with @Replaces cannot also be annotated with @AutoInitialize, @AutoBind, @AutoBindIntoSet, or @AutoBindIntoMap"

        fun missingBoundType(replacedObject: String, boundType: String, type: String): String =
            "Replacement target $replacedObject is bound to $boundType, but $type does not"

        fun targetIsNotAutoBind(replacedObject: String): String =
            "Replacement target $replacedObject must be annotated with @AutoBind"
    }
}