package se.ansman.deager.android

import dagger.hilt.InstallIn
import dagger.hilt.android.EarlyEntryPoint
import dagger.hilt.components.SingletonComponent
import se.ansman.deager.EagerInitializer

@EarlyEntryPoint
@InstallIn(SingletonComponent::class)
internal interface DeagerInitializerEntryPoint {
    val eagerInitializer: EagerInitializer
}