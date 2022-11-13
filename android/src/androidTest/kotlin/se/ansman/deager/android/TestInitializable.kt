package se.ansman.deager.android

import se.ansman.deager.Eager
import se.ansman.deager.Initializable
import javax.inject.Inject
import javax.inject.Singleton

@Eager
@Singleton
class TestInitializable @Inject constructor() : Initializable {
    override fun initialize() {
        isInitialized = true
    }

    companion object {
        var isInitialized = false
            private set
    }
}