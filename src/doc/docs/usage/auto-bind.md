# `@AutoBind` Usage 
To bind objects automatically you just need to annotate a class with the `@AutoBind` annotation (or `@AutoBindIntoSet`
for binding into a set and `@AutoBindIntoMap` for binding into a map).

```kotlin
interface Repository

@AutoBind
@Singleton
class RealRepository @Inject constructor() : Repository
```

## Multibindings
If you need to bind a object into a set or map you can use `@AutoBindIntoSet` or `@AutoBindIntoMap` respectively.

```kotlin
@MapKey
annotation class BindingKey(val name: String)

@BindingKey("ExternalResourceCloseable")
@AutoBindIntoSet // Binds ExternalResource as Closeable using @IntoSet
@AutoBindIntoMap // Binds ExternalResource as Closeable using @IntoMap with `BindingKey` as the map key
@Singleton
class ExternalResource @Inject constructor() : Closeable {
    override fun close() {
        
    }
}
```

## Component
AutoDagger will try to infer the component to install the binding in using the scope. The following mapping is used:

| Scope                    | Component                   |
|--------------------------|-----------------------------|
| No scope                 | `SingletonComponent`        |
| `Singleton`              | `SingletonComponent`        |
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
@AutoBind(inComponent = SomeComponent::class)
class ExternalResource @Inject constructor() : Closeable {
    override fun close() {
        
    }
}
```

## Binding multiple types
If your object has multiple direct supertypes, you need to specify which ones to bind explicitly using the `asTypes`
parameter:
```kotlin
@AutoBind(asTypes = [Closeable::class])
class ExternalResource @Inject constructor() : Runnable, Closeable {
    override fun run() {
        
    }
}
```

You can bind multiple types, but only direct supertypes can be bound (see [limitations](../limitations.md#autobind-only-supports-direct-supertypes)).