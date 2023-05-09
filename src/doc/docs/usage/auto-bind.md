# `@AutoBind` Usage 
To bind objects automatically you just need to annotate a class with the `@AutoBind` annotation (or `@AutoBindIntoSet`
for binding into a set and `@AutoBindIntoMap` for binding into a map).

```kotlin
interface Repository

@AutoBind
@Singleton
class RealRepository @Inject constructor() : Repository

// Generates the equivalent to:
@Binds
abstract fun RealRepository.bindRealRepositoryAsRepository(): Repository
```

## Multibindings
If you need to bind a object into a set or map you can use `@AutoBindIntoSet` or `@AutoBindIntoMap` respectively.

```kotlin
// Binds ExternalResource as Closeable using @IntoSet
@AutoBindIntoSet
// Binds ExternalResource as Closeable using @IntoMap with
// `StringKey` as the map key
@AutoBindIntoMap
@StringKey("ExternalResourceCloseable")
@Singleton
class ExternalResource @Inject constructor() : Closeable {
    override fun close() {}
}

// Generates the equivalent to:
@Binds
@IntoSet
abstract fun ExternalResource.bindExternalResourceAsCloseableIntoSet(): Closeable

@Binds
@IntoMap
@StringKey("ExternalResourceCloseable")
abstract fun ExternalResource.bindExternalResourceAsCloseableIntoMap(): Closeable
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
@AutoBind(inComponent = SomeComponent::class)
class ExternalResource @Inject constructor() : Closeable {
    override fun close() {}
}
```

## Binding multiple types
If your object has multiple direct supertypes, you need to specify which ones to bind explicitly using the `asTypes`
parameter:
```kotlin
@AutoBind(asTypes = [Closeable::class])
class ExternalResource @Inject constructor() : Runnable, Closeable {
    override fun run() {}
    override fun close() {}
}
```

You can bind multiple types, but only direct supertypes can be bound (see [limitations](../limitations.md#autobind-only-supports-direct-supertypes)).

## Objects
Normally the bound object needs to be provided to the dependency graph using either an `@Provides` annotated method
or using an `@Inject` annotated constructor.

Auto Dagger allows you to annotate a Kotlin object with `@AutoBind` without it being provided in the graph.
This is especially useful for tests:
```kotlin
@AutoBind
object DirectExecutor : Executor {
  override fun execute(command: Runnable) {
    command.run()
  }
}
```

## Generics
When using multibindings, it's sometimes useful to be able to bind generic types as wildcard.

Auto Dagger will, by default, bind the exact type of the supertype. But using the `bindGenericAs` parameter you can
chose to bind it as a wildcard instead.

```kotlin
// This will bind StringCallable as Callable<*>
@AutoBindIntoSet(bindGenericAs = BindGenericAs.Wildcard)
class StringCallable @Inject constructor() : Callable<String> {
    override fun call(): String = ""
}
```

There are 3 options for `bindGenericAs`:
- `Type` - Binds the object to the exact supertype (`Callable<String>` in the example above). This is the default. 
- `Wildcard` - Binds the object using wildcards (`Callable<*>` in the example above).
- `TypeAndWildcard` - Binds the object as both the exact type and using wildcards.