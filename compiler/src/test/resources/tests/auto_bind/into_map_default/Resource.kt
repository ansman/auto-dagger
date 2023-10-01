package tests.auto_bind.into_map_default

import dagger.multibindings.StringKey
import se.ansman.dagger.auto.AutoBindIntoMap
import se.ansman.dagger.auto.BindGenericAs
import javax.inject.Inject
import javax.inject.Singleton

@BindGenericAs.Default(BindGenericAs.Wildcard)
interface Resource<T> {
    fun produce(): T
    fun close()
}

@AutoBindIntoMap
@StringKey("test")
@Singleton
class SomeResource @Inject constructor() : Resource<String> {
    override fun produce(): String = ""
    override fun close() {}
}