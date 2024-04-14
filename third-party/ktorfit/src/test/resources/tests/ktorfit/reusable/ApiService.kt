package tests.ktorfit.reusable

import dagger.Reusable
import de.jensklingenberg.ktorfit.http.GET
import se.ansman.dagger.auto.ktorfit.AutoProvideService

@Reusable
@AutoProvideService
interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<String>
}