package se.ansman.dagger.auto.compiler

import se.ansman.dagger.auto.BindGenericAs
import kotlin.reflect.KClass

object Errors {
    fun genericType(annotation: KClass<out Annotation>) =
        genericType(annotation.simpleName!!)

    fun genericType(annotation: String) =
        "Objects annotated with @${annotation.substringAfterLast('.')} cannot be generic"

    fun invalidComponent(component: String): String =
        "The specified component $component isn't a Dagger component"

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
        const val noSuperTypes = "There are no supertypes so there is nothing to bind. Make sure you implement an interface or extend a class to use @AutoBind."
        const val multipleSuperTypes = "Multiple supertypes found. Use the `asTypes` parameter to specify which types to bind"

        fun missingBoundType(boundType: String): String =
            "$boundType is specified using `asTypes` but isn't a supertype"

        fun missingDirectSuperType(boundType: String): String =
            "$boundType is specified using `asTypes` but isn't a direct supertype"

        fun parentComponent(installIn: String, inferredComponent: String): String =
            "The installIn component $installIn is a parent component of $inferredComponent and cannot be used as installIn for @AutoBind"

        fun nonStandardScope(scope: String): String =
            "Scope $scope is not a standard scope so you must specify a component explicitly using the `inComponent` parameter."

        fun invalidBindMode(bindGenericAs: BindGenericAs): String =
            "Using BindGenericAs.${bindGenericAs.name} requires at least one generic supertype."

        object BindGenericAsDefault {
            const val nonGenericType = "@BindGenericAs.Default can only be applied to generic types."
            const val nonAbstractType = "@BindGenericAs.Default can only be applied to interfaces and abstract/open classes."
        }
    }

    object Replaces {
        const val isAutoBindOrInitialize = "Objects annotated with @Replaces cannot also be annotated with @AutoInitialize, @AutoBind, @AutoBindIntoSet, or @AutoBindIntoMap"

        fun missingBoundType(replacedObject: String, boundType: String, type: String): String =
            "Replacement target $replacedObject is bound to $boundType, but $type does not"

        fun targetIsNotAutoBind(replacedObject: String): String =
            "Replacement target $replacedObject must be annotated with @AutoBind"
    }

    interface ApiService {
        val nonInterface: String
        val privateType: String
        val emptyService: String
        val propertiesNotAllowed: String
        val invalidServiceMethod: String
        val scopeAndReusable: String
        fun invalidScope(scope: String, component: String, neededScope: String): String

    }

    object Retrofit : ApiService{
        override val nonInterface = "Only interfaces can be annotated with @AutoProvideService."
        override val privateType = "@AutoProvideService annotated types must not be private."
        override val emptyService = "@AutoProvideService annotated types must have at least one method."
        override val propertiesNotAllowed = "Retrofit services cannot contain properties."
        override val invalidServiceMethod = "Methods in retrofit services must be annotated with a HTTP method annotation such as @GET."
        override val scopeAndReusable = "You cannot mix a scope and @Reusable on the same type. Remove the scope or @Reusable."

        override fun invalidScope(scope: String, component: String, neededScope: String) =
            "You cannot use @$scope when installing in $component, use @$neededScope instead."
    }

    object Ktorfit : ApiService by Retrofit {
        override val propertiesNotAllowed = "Ktorfit services cannot contain properties."
        override val invalidServiceMethod = "Methods in ktorfit services must be annotated with a HTTP method annotation such as @GET."
    }

    object AndroidX {
        object Room {
            const val notADatabase = "Types annotated @AutoProvideDao must be annotated with @Database and directly extend RoomDatabase."
            const val typeMustDirectlyExtendRoomDatabase = "Indirect inheritance of RoomDatabase is not supported right now. If this is needed, open a feature request at https://github.com/ansman/auto-dagger/issues/new and explain your use case."
        }
    }
}
