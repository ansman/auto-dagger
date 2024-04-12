package tests.ktorfit.non_public

import de.jensklingenberg.ktorfit.http.GET
import se.ansman.dagger.auto.ktorfit.AutoProvideService

@AutoProvideService
internal interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<String>
}
