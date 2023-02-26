package se.ansman.dagger.auto.compiler

object Errors {
    object AutoInitialize {
        const val wrongScope = "Objects annotated with @AutoInitialize must also be annotated with @Singleton"
        const val unscopedType = wrongScope
        const val methodInNonModule =
            "@AutoInitialize annotated methods must be declared inside a @Module annotated class, object or interface."
        const val invalidAnnotatedMethod = "Annotated methods must have either the @Provides or @Binds annotation"
    }

    object AutoBind {
        const val genericType = "Auto bound objects cannot be generic"
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
}