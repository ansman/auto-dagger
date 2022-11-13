package se.ansman.deager.android

import android.content.Context
import androidx.startup.Initializer
import dagger.hilt.android.EarlyEntryPoints
import se.ansman.deager.EagerInitializer
import javax.inject.Singleton


/**
 * An [Initializer] that will initialize a [Singleton] scoped [EagerInitializer].
 *
 * This will be called by androidx.startup, provides no result and has no dependencies.
 */
class DeagerInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        EarlyEntryPoints
            .get(context.applicationContext, DeagerInitializerEntryPoint::class.java)
            .eagerInitializer
            .initialize()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}