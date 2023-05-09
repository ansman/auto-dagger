package se.ansman.dagger.auto

/**
 * How generic types are bound when using multibinding such as [AutoBindIntoSet] or [AutoBindIntoMap].
 */
public enum class BindGenericAs {
    /**
     * Only the exact supertype is bound. For example, if the type is `List<String>` then only `List<String>` is bound.
     *
     * This is the default.
     */
    Type,

    /**
     * The type is bound as a wildcard type. For example, if the type is `List<String>` then it's bound as `List<*>`.
     */
    Wildcard,

    /**
     * The type is bound as the exact supertype and as a wildcard type. For example, if the type is `List<String>` then
     * it's bound as both `List<String>` and `List<*>`.
     */
    TypeAndWildcard
}