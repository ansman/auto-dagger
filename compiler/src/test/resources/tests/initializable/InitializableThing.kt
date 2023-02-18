package se.ansman.initializable

import se.ansman.dagger.auto.AutoInitialize
import se.ansman.dagger.auto.Initializable
import javax.inject.Inject
import javax.inject.Singleton

@AutoInitialize
@Singleton
class InitializableThing @Inject constructor() : Initializable {
    override fun initialize() {}
}