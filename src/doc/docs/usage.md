# Usage
Simply annotate any `@Singleton` scoped object, provider or binding with `@Eager` to make it be eagerly initialized.

```kotlin
@Eager
@Singleton
class SomeRepository @Inject constructor() {
    init {
        // This will be executed at application startup, even if nobody injects it.
    }
}
```

## Initializable
It's often considered bad practice to have side effects in a constructor/initializer block. If this is a concern your
object can implement the `Initializable` interface. Deager will then call `initialize` when it's time to initialize.

```kotlin
@Eager
@Singleton
class SomeRepository @Inject constructor() : Initializable {
    override fun initialize() {
        // This will be executed at application startup, even if nobody injects it.
    }
}
```

## AndroidX Startup
Deager uses [AndroidX Startup](https://developer.android.com/topic/libraries/app-startup) to initialize objects eagerly.

If you prefer to not use this you can just depend on the `core` artifact which won't pull in AndroidX Startup.

To initialize the objects manually, use `EagerInitializer`.

```kotlin
@HiltAndroidApp
class TheApp : Application() {
    @Inject
    lateinit var eagerInitializer: EagerInitializer
    
    override fun onCreate() {
        super.onCreate()
        eagerInitializer.initialize()
    }
}
```