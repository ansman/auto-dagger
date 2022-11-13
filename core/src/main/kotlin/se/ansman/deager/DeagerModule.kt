package se.ansman.deager

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet

@Module
@InstallIn(SingletonComponent::class)
internal object DeagerModule {
    @Provides
    @ElementsIntoSet
    fun primeInitializables(): Set<Initializable> = emptySet()
}