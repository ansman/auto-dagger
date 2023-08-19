package tests.retrofit.qualifiers

import retrofit2.http.GET
import se.ansman.dagger.auto.retrofit.AutoProvideService
import javax.inject.Named

@AutoProvideService
@Named("api1")
interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<String>
}