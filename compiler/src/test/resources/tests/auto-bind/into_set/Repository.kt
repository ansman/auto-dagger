package se.ansman.into_set

import se.ansman.dagger.auto.AutoBindIntoSet
import java.io.Closeable
import javax.inject.Inject
import javax.inject.Singleton

@AutoBindIntoSet
@Singleton
class Repository @Inject constructor() : Closeable {
    override fun close() {}
}