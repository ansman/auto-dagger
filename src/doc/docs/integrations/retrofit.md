# Integration with Retrofit 
If you use [Retrofit](https://square.github.io/retrofit/) to define your API services, you can use the 
`@AutoProvideService` to automatically contribute your services to the Dagger graph.

Firstly add the required dependency:
```kotlin
dependencies {
    implementation("se.ansman.dagger.auto:retrofit:{{gradle.version}}")
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

You'll also need to provide a Retrofit instance to the component you want to inject the service into:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
  @Provides
  @Singleton
  fun provideRetrofit(): Retrofit = Retrofit.Builder()
      // Perform any setup you need
      .baseUrl("https://api.example.com/")
      .addConverterFactory(SomeConverterFactory.create())
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
If you have multiple retrofit instances and use qualifiers to differentiate them, you can specify the qualifier on the
service:
```kotlin
@AutoProvideService
@Named("api1")
class ApiService {
    // Service methods
}
```

## Making the service scoped or reusable
By default, provided service is unscoped and will be created every time it is injected. Retrofit caches the parsing of
services so this is not an expensive operation, but it injected frequently you can avoid this by annotating your service
with a scope or `@Reusable`:
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