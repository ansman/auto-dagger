# Limitations

## `@AutoInitializable` objects must be `@Singleton` scoped
For now, all initializable objects must be `@Singleton` scoped.

This is because the semantics are unclear if an objects are not scoped since `AutoDaggerInitializable` is not scoped so 
creating multiple instances of `AutoDaggerInitializable` would created multiple instances of unscoped objects which might be
unexpected.

This limitation might be lifted in the future. If you have a use case for unscoped objects please 
[file an issue](https://github.com/ansman/auto-dagger/issues/new).

## `@AutoBind` objects must not be generic
Since Auto Dagger cannot determine actual type arguments, generic objects cannot be automatically bound.

## `@AutoBind` only supports direct supertypes
You can only bind objects to their direct supertypes. For example this isn't supported:
```kotlin
interface Closeable

interface Repository : Closeable

// This won't work because `RealRepository` doesn't 
// directly implement `Closeable`
@AutoBindIntoSet(asTypes = [Closeable::class])
@Singleton
class RealRepository @Inject constructor() : Repository
```

To solve this just add an explicit supertype:
```kotlin
@AutoBindIntoSet(asTypes = [Closeable::class])
@Singleton
class RealRepository @Inject constructor() : Repository, Closeable
```