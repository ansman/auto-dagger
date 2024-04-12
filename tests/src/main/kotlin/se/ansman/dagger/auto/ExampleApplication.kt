package se.ansman.dagger.auto

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import se.ansman.dagger.auto.androidx.room.UserDao
import se.ansman.dagger.auto.retrofit.ExampleRetrofitApiService
import javax.inject.Inject

@HiltAndroidApp
class ExampleApplication : Application() {
    @Inject
    lateinit var apiService: ExampleRetrofitApiService

    @Inject
    lateinit var users: UserDao
}