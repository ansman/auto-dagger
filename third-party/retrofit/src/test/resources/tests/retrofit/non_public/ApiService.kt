package tests.retrofit.non_public

import retrofit2.http.GET
import se.ansman.dagger.auto.retrofit.AutoProvideService

@AutoProvideService
internal interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<String>
}
