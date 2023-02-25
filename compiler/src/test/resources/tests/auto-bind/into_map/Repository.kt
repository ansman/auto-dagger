package se.ansman.into_map

import dagger.MapKey
import se.ansman.dagger.auto.AutoBindIntoMap
import java.io.Closeable
import javax.inject.Inject
import javax.inject.Singleton

@MapKey
annotation class BindingKey(val name: String)

@AutoBindIntoMap
@BindingKey("test")
@Singleton
class Repository @Inject constructor() : Closeable {
    override fun close() {}
}