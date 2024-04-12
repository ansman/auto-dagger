package se.ansman.dagger.auto.ktorfit

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KtorfitModule {
    @Provides
    @Singleton
    fun provideKtorfit(): Ktorfit =
        Ktorfit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .build()
}