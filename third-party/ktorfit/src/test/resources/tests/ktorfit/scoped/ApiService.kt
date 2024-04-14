package tests.ktorfit.scoped

import de.jensklingenberg.ktorfit.http.GET
import se.ansman.dagger.auto.ktorfit.AutoProvideService
import javax.inject.Singleton

@Singleton
@AutoProvideService
interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<String>
}