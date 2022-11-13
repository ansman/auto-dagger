package se.ansman.deager.tests

import se.ansman.deager.Eager
import javax.inject.Inject
import javax.inject.Singleton

@Eager
@Singleton
class Repository @Inject constructor() {
    init {
        ++createCount
    }

    companion object {
        var createCount = 0
    }
}