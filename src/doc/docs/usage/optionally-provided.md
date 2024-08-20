# `@OptionallyProvided` Usage
Denotes that there might not be an implementation of the given type.

This will generate a `@BindsOptionalOf` binding for the annotated type.

This is useful when the implementation will only be available in some types of builds. For example
an Android app might have some debug settings that are only available in debuggable builds.

```kotlin
@OptionallyProvided
interface DebugApi

// This generates
@BindsOptionalOf
abstract fun bindsOptionalDebugApi(): DebugApi

// Then in your implementation module
@AutoBind
@Singleton
class RealDebugApi @Inject constructor() : DebugApi
```

## Component
AutoDagger will try to infer the component to install the binding in using the scope. The following mapping is used:

| Scope                    | Component                   |
|--------------------------|-----------------------------|
| No scope                 | `SingletonComponent`        |
| `Singleton`              | `SingletonComponent`        |
| `Reusable`               | `SingletonComponent`        |
| `ActivityRetainedScoped` | `ActivityRetainedComponent` |
| `ActivityScoped`         | `ActivityComponent`         |
| `FragmentScoped`         | `FragmentComponent`         |
| `ServiceScoped`          | `ServiceComponent`          |
| `ViewScoped`             | `ViewComponent`             |
| `ViewModelScoped`        | `ViewModelComponent`        |
| `ViewWithFragmentScoped` | `ViewWithFragmentComponent` |

If you are are using custom scopes or want to change which component the binding is installed in, you can use the
`inComponent` property:
```kotlin
@OptionallyProvided(inComponent = SomeComponent::class)
interface DebugApi
```

## Qualifiers
Any [Qualifier]s on the annotated type will be carried over to the binding:
```
@Named("Prod")
@OptionallyProvided
interface Authenticator

// Generates the equivalent to:
@BindsOptionalOf
@Named("Prod")
abstract fun bindsOptionalAuthenticator(): Authenticator
```