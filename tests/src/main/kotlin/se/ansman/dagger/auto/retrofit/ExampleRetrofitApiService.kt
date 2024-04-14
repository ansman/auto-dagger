package se.ansman.dagger.auto.retrofit

import retrofit2.http.GET

@AutoProvideService
interface ExampleRetrofitApiService {
    @GET("/users")
    suspend fun getUsers(): List<String>
}