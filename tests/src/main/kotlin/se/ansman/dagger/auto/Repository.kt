package se.ansman.dagger.auto

import dagger.multibindings.StringKey
import java.io.Closeable
import javax.inject.Inject
import javax.inject.Singleton

@AutoInitialize
@AutoBind
@AutoBindIntoSet
@AutoBindIntoMap
@StringKey("test")
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