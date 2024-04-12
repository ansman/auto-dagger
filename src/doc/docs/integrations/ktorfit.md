# Integration with Ktorfit 
If you use [Ktorfit](https://foso.github.io/Ktorfit/) to define your API services, you can use the 
`@AutoProvideService` to automatically contribute your services to the Dagger graph.

Firstly add the required dependency:
```kotlin
dependencies {
    implementation("se.ansman.dagger.auto:ktorfit:{{gradle.version}}")
    kapt("se.ansman.dagger.auto:compiler:{{gradle.version}}")
    // or if you use KSP
    ksp("se.ansman.dagger.auto:compiler:{{gradle.version}}")
}
```
    
Then annotate your service with `@AutoProvideService`:
```kotlin
@AutoProvideService
class ApiService {
  @GET("users")
  suspend fun getUsers(): List<User>
}
```

You'll also need to provide a Ktorfit instance to the component you want to inject the service into:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object KtorfitModule {
  @Provides
  @Singleton
  fun provideKtorfit(): Ktorfit = Ktorfit.Builder()
      // Perform any setup you need
      .baseUrl("https://api.example.com/")
      .build()
}
```

## Changing the target component
By default, services are installed in the `SingletonComponent`. If you'd like to change this you can do so by
specifying the `inComponent` parameter:
```kotlin
@AutoProvideService(inComponent = SomeOtherComponent::class)
class ApiService {
    // Service methods
}
```

## Qualifiers
If you have multiple ktorfit instances and use qualifiers to differentiate them, you can specify the qualifier on the
service:
```kotlin
@AutoProvideService
@Named("api1")
class ApiService {
    // Service methods
}
```

## Making the service scoped or reusable
If you'd like to cache/reuse the service instances you can annotate the service with a scope or `@Reusable`:
```kotlin
@Singleton // This must match the `inComponent` parameter of @AutoProvideService
@AutoProvideService
class ApiService {
    // Service methods
}

// Or if you want to make it reusable
@Reusable
@AutoProvideService
class ApiService {
    // Service methods
}
```