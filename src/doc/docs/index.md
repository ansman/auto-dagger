Auto Dagger
===
Auto Dagger allows you to automate some Dagger setup using Hilt.

For example you can use the @AutoInitialize annotation to automatically initialize objects during app launch and you can use the @AutoBind annotation to automatically bind objects.

Automatic initialization is done using AndroidX Startup.

You can find the project on GitHub in the [ansman/auto-dagger](https://github.com/ansman/auto-dagger) repo.

If you're looking javadoc/dokka you can find it here [here]({{ gradle.dokkaLink }}).

To get set up see [getting started](getting-started.md).

## Examples
### `@AutoBind`
```kotlin
interface Repository

@AutoBind
@Singleton
class RealRepository @Inject constructor() : Repository
```

For more detailed documentation see the [`@AutoBind` usage](usage/auto-bind.md).

### `@AutoInitialize`
```kotlin
@AutoInitialize
@Singleton
class SomeRepository @Inject constructor() {
    init {
        // This will be executed at application startup, even if nobody injects it.
    }
}
```

For more detailed documentation see the [`@AutoInitialize` usage](usage/auto-initialize.md).
