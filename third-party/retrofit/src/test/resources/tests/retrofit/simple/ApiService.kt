package tests.retrofit.simple

import retrofit2.http.GET
import se.ansman.dagger.auto.retrofit.AutoProvideService

@AutoProvideService
interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<String>
}