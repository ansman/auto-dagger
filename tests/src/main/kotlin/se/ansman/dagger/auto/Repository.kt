package se.ansman.dagger.auto

import javax.inject.Inject
import javax.inject.Singleton

@AutoInitialize
@Singleton
class Repository @Inject constructor() {
    init {
        ++createCount
    }

    companion object {
        var createCount = 0
    }
}