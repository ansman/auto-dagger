# `@AutoInitialize` Usage 
Simply annotate any `@Singleton` scoped object, provider or binding with `@AutoInitialize` to make it be automatically 
initialized when your application launches.

```kotlin
@AutoInitialize
@Singleton
class SomeRepository @Inject constructor() {
    init {
        // This will be executed at application startup, even if nobody injects it.
    }
}
```

## Initializable
It's often considered bad practice to have side effects in a constructor/initializer block. If this is a concern your
object can implement the `Initializable` interface. Auto Dagger will then call `initialize` when it's time to initialize.

```kotlin
@AutoInitialize
@Singleton
class SomeRepository @Inject constructor() : Initializable {
    override fun initialize() {
        // This will be executed at application startup, even if nobody injects it.
    }
}
```

## Priority
By default objects are initialized in an undefined order. This can be changed by setting a priority.

Objects are initialized in descending priority with the default priority being `1`. Setting the priority to 0 or below
makes the object be initialized after objects with the default priority and setting the priority to 2 or above 
initializes it before.

```kotlin
@AutoInitialize(priority = 4711)
@Singleton
class ImportantRepository @Inject constructor() {
    override fun initialize() {
        // This will be called earlier than objects without a priority
    }
}
```

## AndroidX Startup
Auto Dagger uses [AndroidX Startup](https://developer.android.com/topic/libraries/app-startup) to initialize objects 
at startup.

If you prefer to not use this you can just depend on the `core` artifact which won't pull in AndroidX Startup.

To initialize the objects manually, use `AutoDaggerInitializer`.

```kotlin
@HiltAndroidApp
class TheApp : Application() {
    @Inject
    lateinit var initializer: AutoDaggerInitializer
    
    override fun onCreate() {
        super.onCreate()
        initializer.initialize()
    }
}
```