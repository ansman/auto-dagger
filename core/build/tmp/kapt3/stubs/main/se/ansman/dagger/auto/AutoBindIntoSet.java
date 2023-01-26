package se.ansman.dagger.auto;

/**
 * A version of [AutoBind] that binds the object using [IntoSet].
 *
 * For more documentation on auto bind see [AutoBind].
 *
 * See also [Set Multibindings](https://dagger.dev/dev-guide/multibindings.html#set-multibindings)
 *
 * @param inComponent Which component to install the binding in. Defaults to being inferred based on the scope.
 * @param asTypes Specifies which supertypes to bind the object as. Required if there are multiple supertypes.
 * @param bindGenericAs Specifies how generic supertypes should be bound. Defaults to [BindGenericAs.ExactType] or if the
 *  target type is annotated with [BindGenericAs.Default].
 * @see AutoBind
 * @see AutoBindIntoSet
 * @see IntoSet
 * @see BindGenericAs
 * @see BindGenericAs.Default
 * @since 1.0.0
 */
@kotlin.annotation.Target(allowedTargets = {kotlin.annotation.AnnotationTarget.CLASS})
@kotlin.annotation.Retention(value = kotlin.annotation.AnnotationRetention.BINARY)
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.CLASS)
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0087\u0002\u0018\u00002\u00020\u0001B,\u0012\f\b\u0002\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u0003\u0012\u0012\b\u0002\u0010\u0004\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00030\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007R\u0019\u0010\u0004\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00030\u0005\u00a2\u0006\u0006\u001a\u0004\b\u0004\u0010\bR\u000f\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\tR\u0013\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u0003\u00a2\u0006\u0006\u001a\u0004\b\u0002\u0010\n\u00a8\u0006\u000b"}, d2 = {"Lse/ansman/dagger/auto/AutoBindIntoSet;", "", "inComponent", "Lkotlin/reflect/KClass;", "asTypes", "", "bindGenericAs", "Lse/ansman/dagger/auto/BindGenericAs;", "()[Ljava/lang/Class;", "()Lse/ansman/dagger/auto/BindGenericAs;", "()Ljava/lang/Class;", "core"})
@dagger.hilt.GeneratesRootInput()
public abstract @interface AutoBindIntoSet {
    
    public abstract java.lang.Class<?> inComponent() default java.lang.Void.class;
    
    public abstract java.lang.Class<?>[] asTypes() default {};
    
    public abstract se.ansman.dagger.auto.BindGenericAs bindGenericAs() default se.ansman.dagger.auto.BindGenericAs.ExactType;
}