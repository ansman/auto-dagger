package tests.ktorfit.simple

import de.jensklingenberg.ktorfit.http.GET
import se.ansman.dagger.auto.ktorfit.AutoProvideService

@AutoProvideService
interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<String>
}