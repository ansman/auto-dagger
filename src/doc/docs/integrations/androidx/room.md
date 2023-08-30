# Integration with AndroidX Room 
If you use [AndroidX Room](https://developer.android.com/topic/libraries/architecture/room) in your project,
Auto Dagger can automatically provides some utilities for working with databases.

To get started, add the dependency:
```kotlin
dependencies {
    implementation("se.ansman.dagger.auto:androidx-room:{{gradle.version}}")
    kapt("se.ansman.dagger.auto:compiler:{{gradle.version}}")
    // or if you use KSP
    ksp("se.ansman.dagger.auto:compiler:{{gradle.version}}")
}
```

## Automatically providing DAOs
You can annotate your `RoomDatabase` with `@AutoProvideDaos` to automatically provide all DAOs in the database:
```kotlin
@Database(entities = [User::class], version = 1)
@AutoProvideDaos
abstract class AppDatabase : RoomDatabase() {
    abstract val users: UserDao
}
```

Now you can inject `UserDao` directly.

By default, the DAOs will be provided in the `SingletonComponent`. If you want to change the component you can use the
`inComponent` parameter:
```kotlin
@Database(entities = [User::class], version = 1)
@AutoProvideDaos(inComponent = SomeOtherComponent::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val users: UserDao
}
```

For now, the database must directly extend `RoomDatabase`. Having it as an indirect superclass is not supported.
If you need support for this open a [feature request](https://github.com/ansman/auto-dagger/issues/new) and detail your
use case.