package tests.ktorfit.non_public_parent

import de.jensklingenberg.ktorfit.http.GET
import se.ansman.dagger.auto.ktorfit.AutoProvideService

internal object Wrapper1 {
    object Wrapper2 {
        @AutoProvideService
        interface ApiService {
            @GET("users")
            suspend fun getUsers(): List<String>
        }
    }
}
