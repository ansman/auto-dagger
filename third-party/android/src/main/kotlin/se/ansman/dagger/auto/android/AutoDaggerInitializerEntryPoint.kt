package se.ansman.dagger.auto.android

import dagger.hilt.InstallIn
import dagger.hilt.android.EarlyEntryPoint
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.AutoDaggerInitializer

@EarlyEntryPoint
@InstallIn(SingletonComponent::class)
internal interface AutoDaggerInitializerEntryPoint {
    val initializable: AutoDaggerInitializer
}