package se.ansman.initializable

import se.ansman.deager.Eager
import se.ansman.deager.Initializable
import javax.inject.Inject
import javax.inject.Singleton

@Eager
@Singleton
class InitializableThing @Inject constructor() : Initializable {
    override fun initialize() {}
}