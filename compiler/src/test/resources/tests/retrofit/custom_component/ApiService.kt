package tests.retrofit.custom_component

import dagger.hilt.android.components.FragmentComponent
import retrofit2.http.GET
import se.ansman.dagger.auto.retrofit.AutoProvideService

@AutoProvideService(inComponent = FragmentComponent::class)
interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<String>
}