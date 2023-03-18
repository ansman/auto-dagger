package se.ansman.dagger.auto.android

import android.content.Context
import androidx.startup.AppInitializer
import androidx.startup.Initializer
import dagger.hilt.android.EarlyEntryPoints
import javax.inject.Singleton

/**
 * An [Initializer] that will initialize a [Singleton] scoped [AutoDaggerStartupInitializer].
 *
 * This will be called by androidx.startup, provides no result and has no dependencies.
 *
 * @since 0.2
 */
class AutoDaggerStartupInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        check(AppInitializer.getInstance(context).isEagerlyInitialized(javaClass)) {
            "AutoDaggerInitializer cannot be initialized lazily."
        }

        EarlyEntryPoints.get(context.applicationContext, AutoDaggerInitializerEntryPoint::class.java)
            .initializable
            .initialize()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}