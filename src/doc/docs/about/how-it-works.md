# How it works

## `@AutoBind`
When you annotate an object with `@AutoBind`, Auto Dagger will generate a component which is installed
into the inferred (or explicitly provided) component using Hilt.

Given this class:
```kotlin
interface Repository

@AutoBind
@Singleton
class RealRepository @Inject constructor() : Repository
```

Auto Dagger will generate this module:
```java
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(topLevelClass = RealRepository.class)
public abstract class AutoBindRealRepositorySingletonModule {
    private AutoBindRealRepositorySingletonModule() {}

    @Binds
    public abstract Repository bindRealRepositoryAsRepository(RealRepository realRepository);
}
```

## `@AutoInitialize`
When you annotate an object, binding or provider with `@AutoInitialize`, Auto Dagger will generate a module which is
installed into the `SingletonComponent` using Hilt.

Given this class:
```kotlin
@AutoInitialize
@Singleton
class Repository @Inject constructor()
```

Auto Dagger will generate this module:
```java
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(topLevelClass = Repository.class)
public final class AutoInitializeRepositoryModule {
    private AutoInitializeRepositoryModule() {}
    
    @Provides
    @IntoSet
    public static Initializable provideRepositoryAsInitializable(Lazy<Repository> lazyRepository) {
        return Initializable.fromLazy(lazyRepository);
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