package se.ansman.deager.tests

import se.ansman.deager.Eager
import se.ansman.deager.Initializable
import javax.inject.Inject
import javax.inject.Singleton

@Eager
@Singleton
class InitializableRepository @Inject constructor() : Initializable {
    init {
        ++createCount
    }

    override fun initialize() {
        ++initCount
    }

    companion object {
        var createCount = 0
        var initCount = 0
    }
}