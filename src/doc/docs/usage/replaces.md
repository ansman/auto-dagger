# `@Replaces` Usage 
During tests it's often useful to swap out real dependencies for fake dependencies. If you're using `@AutoBind` to
bind your dependencies then it's trivial to swap out dependencies during tests using the `@Replaces` annotation:
```kotlin
interface Repository

@AutoBind
@Singleton
class RealRepository @Inject constructor() : Repository

// In your test source set
@Replaces(RealRepository::class)
@Singleton
class FakeRepository @Inject constructor() : Repository
```

To use `@Replaces` there are some requirements:

- The replacement must implement the types bound the target. 
- The replacement target must be annotated with `@AutoBind`.
- The replacement must not be annotated with `@AutoBind`, `@AutoBindIntoSet`, `@AutoBindIntoMap`, or `@AutoInitialize`.

## Multibindings
If the replaced type uses multibindings (`@AutoBindIntoSet` or `@AutoBindIntoMap`) then those are only replaced if
the annotated type also implements them. Otherwise the multibinding is removed.

So for example. If the target type uses `@AutoBindIntoSet` to bind it as a `Closeable` but your replacement doesn't 
implement `Closeable` then that binding is removed. If it does implement `Closeable` then the binding is replaced with
the fake binding.

## Auto Initialize
If the target type is annotated with `@AutoInitialize`, then an empty module will be generated to replace the auto
initialize module, effectively removing it.

## Objects
Normally the replacement object needs to be provided to the dependency graph using either an `@Provides` annotated
method or using an `@Inject` annotated constructor.

Auto Dagger allows you to annotate a Kotlin object with `@Replaces` without it being provided in the graph.
This is especially useful for tests:
```kotlin
@Replaces(ThreadPoolExecutor::class)
object DirectExecutor : Executor {
  override fun execute(command: Runnable) {
    command.run()
  }
}
```
