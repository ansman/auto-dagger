package tests.ktorfit.qualifiers

import de.jensklingenberg.ktorfit.http.GET
import se.ansman.dagger.auto.ktorfit.AutoProvideService
import javax.inject.Named

@AutoProvideService
@Named("api1")
interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<String>
}