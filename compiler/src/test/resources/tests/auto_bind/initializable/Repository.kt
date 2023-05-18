package tests.auto_bind.initializable

import se.ansman.dagger.auto.AutoBind
import se.ansman.dagger.auto.AutoInitialize
import se.ansman.dagger.auto.Initializable
import javax.inject.Inject
import javax.inject.Singleton

interface Repository

@AutoBind
@AutoInitialize
@Singleton
class RealRepository @Inject constructor() : Repository, Initializable {
    override fun initialize() {}
}