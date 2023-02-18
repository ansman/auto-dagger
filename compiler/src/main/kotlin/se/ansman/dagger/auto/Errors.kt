package se.ansman.dagger.auto

object Errors {
    const val wrongScope = "Objects annotated with @AutoInitialize must also be annotated with @Singleton"
    const val unscopedType = wrongScope
    const val methodInNonModule = "@AutoInitialize annotated methods must be declared inside a @Module annotated class, object or interface."
    const val invalidAnnotatedMethod = "Annotated methods must have either the @Provides or @Binds annotation"
}