package tests.auto_bind.into_set_default

import se.ansman.dagger.auto.AutoBindIntoSet
import se.ansman.dagger.auto.BindGenericAs
import javax.inject.Inject
import javax.inject.Singleton

@BindGenericAs.Default(BindGenericAs.Wildcard)
interface Resource<T> {
    fun produce(): T
    fun close()
}

@AutoBindIntoSet
@Singleton
class SomeResource @Inject constructor() : Resource<String> {
    override fun produce(): String = ""
    override fun close() {}
}