package se.ansman.deager

object Errors {
    const val wrongScope = "Eager objects must be annotated with @Singleton"
    const val unscopedType = wrongScope
    const val methodInNonModule = "@Eager annotated methods must be declared inside a @Module annotated class, object or interface."
    const val invalidAnnotatedMethod = "Annotated methods must have either the @Provides or @Binds annotation"
}