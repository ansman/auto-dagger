package se.ansman.dagger.auto

import dagger.MapKey
import java.io.Closeable
import javax.inject.Inject
import javax.inject.Singleton

@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class BindingKey(val name: String)

@AutoInitialize
@AutoBind
@AutoBindIntoSet
@AutoBindIntoMap
@BindingKey("test")
@Singleton
class Repository @Inject constructor() : Closeable {
    init {
        ++createCount
    }

    override fun close() {}

    companion object {
        var createCount = 0
    }
}