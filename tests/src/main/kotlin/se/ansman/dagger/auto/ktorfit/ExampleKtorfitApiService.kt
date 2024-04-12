package se.ansman.dagger.auto.ktorfit

import de.jensklingenberg.ktorfit.http.GET

@AutoProvideService
interface ExampleKtorfitApiService {
    @GET("/users")
    suspend fun getUsers(): List<String>
}