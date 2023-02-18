package se.ansman.dagger.auto

import javax.inject.Inject
import javax.inject.Singleton

@AutoInitialize
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