package se.ansman.dagger.auto

import javax.inject.Inject
import javax.inject.Singleton

@AutoInitialize
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