package tests.retrofit.reusable

import dagger.Reusable
import retrofit2.http.GET
import se.ansman.dagger.auto.retrofit.AutoProvideService

@Reusable
@AutoProvideService
interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<String>
}