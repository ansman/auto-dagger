# Integration with AndroidX ViewModel 
If you use [AndroidX ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) in your project,
Auto Dagger can automatically provides some utilities for working with view models.

To get started, add the dependency:
```kotlin
dependencies {
    implementation("se.ansman.dagger.auto:androidx-viewmodel:{{gradle.version}}")
}
```

## CoroutineScope
If you use Kotlin Coroutines, you can inject a `CoroutineScope` into your view models. This allows you to use a test
scope when testing your view models.

The scope has the `ViewModelSpecific` qualifier and uses a `SupervisorJob` and the `Dispatchers.Main.immediate` 
dispatcher.

Usage:
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    @ViewModelSpecific
    private val viewModelScope: CoroutineScope
) : ViewModel()
```

## ViewModelSpecific
`ViewModelSpecific` is a qualifier annotation that can be used to inject objects that are specific to a view model, but
might otherwise be added to your graph such as `CoroutineScope`.