# Limitations

## `@AutoInitializable` objects must be `@Singleton` scoped
For now, all initializable objects must be `@Singleton` scoped.

This is because the semantics are unclear if an objects are not scoped since `AutoDaggerInitializable` is not scoped so 
creating multiple instances of `AutoDaggerInitializable` would created multiple instances of unscoped objects which might be
unexpected.

This limitation might be lifted in the future. If you have a use case for unscoped objects please 
[file an issue](https://github.com/ansman/auto-dagger/issues/new).

## Only `@Singleton` is supported
Ideally other scopes would also be supported, but unfortunately because of how Dagger works it's not really possible
to wire this correctly. It's also unclear if other scopes could be automatically initialized like singleton objects.