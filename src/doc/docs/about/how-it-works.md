# How it works

## `@Eager`
When you annotate an object, binding or provider with `@Eager`, Deager will generate a component which is installed into
the `SingletonComponent` using Hilt.

This is an example of such a component:
```java
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = SomeThing.class
)
public final class EagerSomeThingComponent {
    private EagerSomeThingComponent() {}
    
    // If the object implement Initializable then @BindsIntoSet is used instead
    @Provides
    @IntoSet
    public static Initializable provideSomeThingAsInitializable(Lazy<SomeThing> lazySomeThing) {
        return Initializable.fromLazy(lazySomeThing);
    }
}
```

## `EagerInitializer`
`EagerInitializer` is the container for all `Initializable` that should be eagerly initialized. It's provided into the
`@Singleton` component and accepts a set of all the initializables.

When calling `initialize` it will call `initialize` on each object in turn.

Any exceptions are rethrown, but only after all objects have been initialized.

## `DeagerInitializer`
`DeagerInitializer` is the [AndroidX Startup](https://developer.android.com/topic/libraries/app-startup) initializer 
that performs the eager initialization at startup.

## Why does it generate Java?
Given that the library is written in Kotlin, why are the generated files Java files and not Kotlin files?

This is because when using KAPT, which Dagger uses, generating Kotlin files would create a bit of overhead since it 
needs to generate Java stubs for the generated files before continuing with the next round of processing.

By generating Java files the stub generation can be skipped.

Deager has support for KSP so when Dagger supports it, Deager will start generating Kotlin files. 