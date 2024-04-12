package tests.ktorfit.custom_component

import dagger.hilt.android.components.FragmentComponent
import de.jensklingenberg.ktorfit.http.GET
import se.ansman.dagger.auto.ktorfit.AutoProvideService

@AutoProvideService(inComponent = FragmentComponent::class)
interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<String>
}