package se.ansman.dagger.auto

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet

@Module
@InstallIn(SingletonComponent::class)
internal object AutoDaggerModule {
    @Provides
    @ElementsIntoSet
    fun primeInitializables(): Set<Initializable> = emptySet()
}