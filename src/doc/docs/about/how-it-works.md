# How it works

## `@AutoInitialize`
When you annotate an object, binding or provider with `@AutoInitialize`, Auto Dagger will generate a component which is installed
into the `SingletonComponent` using Hilt.

This is an example of such a component:
```java
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = SomeThing.class
)
public final class AutoInitializeSomeThingComponent {
    private AutoInitializeSomeThingComponent() {}
    
    // If the object implements Initializable, then @Binds is used instead of @Provides
    @Provides
    @IntoSet
    public static Initializable provideSomeThingAsInitializable(Lazy<SomeThing> lazySomeThing) {
        return Initializable.fromLazy(lazySomeThing);
    }
}
```

## `AutoDaggerInitializable`
`AutoDaggerInitializable` is the container for all `Initializable` that should be initialized at startup. It's provided
into the `@Singleton` component, and accepts a set of all the initializables.

Calling `AutoDaggerInitializable.initialize` calls `initialize` on each object in turn.

Any exceptions are rethrown, but only after all objects have been initialized. If multiple objects throw, then 
subsequent exceptions are added as suppressed exceptions.

## `AutoDaggerStartupInitializer`
`AutoDaggerStartupInitializer` is the [AndroidX Startup](https://developer.android.com/topic/libraries/app-startup)
initializer that performs the initialization at startup.

## Why does it generate Java?
Given that the library is written in Kotlin, why are the generated files Java files and not Kotlin files?

This is because when using KAPT, which Dagger uses, generating Kotlin files would create a bit of overhead since it 
needs to generate Java stubs for the generated files before continuing with the next round of processing.

By generating Java files the stub generation can be skipped.

Auto Dagger has support for KSP so when Dagger/Hilt supports it, Auto Dagger will start generating Kotlin files. 