package se.ansman.into_map

import dagger.multibindings.StringKey
import se.ansman.dagger.auto.AutoBindIntoMap
import java.io.Closeable
import javax.inject.Inject
import javax.inject.Singleton

@AutoBindIntoMap
@StringKey("test")
@Singleton
class Repository @Inject constructor() : Closeable {
    override fun close() {}
}