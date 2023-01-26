package se.ansman.dagger.auto;

/**
 * Marks the given objects as being initializable.
 *
 * This will instruct Auto Dagger to provide the object as an [Initializable] which allows you to use
 * [AutoDaggerInitializer] to later initialize the annotated objects.
 *
 * If the object implements the [Initializable] interface then it will be created when
 * [AutoDaggerInitializer] is injected and [Initializable.initialize] will be called when
 * [AutoDaggerInitializer.initialize] is called.
 *
 * Otherwise, the object is only created when [AutoDaggerInitializer.initialize] is called.
 *
 * @property priority The priority of the object which determines initialization order. Objects with a higher priority
 *                   are initialized first. If two initializables have the same priority then they are initialized in
 *                   an undefined order. The default priority is 1.
 * @since 1.0.0
 */
@kotlin.annotation.Retention(value = kotlin.annotation.AnnotationRetention.BINARY)
@kotlin.annotation.Target(allowedTargets = {kotlin.annotation.AnnotationTarget.CLASS, kotlin.annotation.AnnotationTarget.FUNCTION, kotlin.annotation.AnnotationTarget.PROPERTY_GETTER})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.CLASS)
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\b\u0087\u0002\u0018\u0000 \u00052\u00020\u0001:\u0001\u0005B\n\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003R\u000f\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0006\u001a\u0004\b\u0002\u0010\u0004\u00a8\u0006\u0006"}, d2 = {"Lse/ansman/dagger/auto/AutoInitialize;", "", "priority", "", "()I", "Companion", "core"})
@dagger.hilt.GeneratesRootInput()
public abstract @interface AutoInitialize {
    public static final int defaultPriority = 1;
    @org.jetbrains.annotations.NotNull()
    public static final se.ansman.dagger.auto.AutoInitialize.Companion Companion = null;
    
    public abstract int priority() default 1;
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lse/ansman/dagger/auto/AutoInitialize$Companion;", "", "()V", "defaultPriority", "", "core"})
    public static final class Companion {
        public static final int defaultPriority = 1;
        
        private Companion() {
            super();
        }
    }
}