package tests.retrofit.scoped

import retrofit2.http.GET
import se.ansman.dagger.auto.retrofit.AutoProvideService
import javax.inject.Singleton

@Singleton
@AutoProvideService
interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<String>
}