package tests.retrofit.non_public_parent

import retrofit2.http.GET
import se.ansman.dagger.auto.retrofit.AutoProvideService

internal object Wrapper1 {
    object Wrapper2 {
        @AutoProvideService
        interface ApiService {
            @GET("users")
            suspend fun getUsers(): List<String>
        }
    }
}
