package se.ansman.initializable

import se.ansman.dagger.auto.AutoBind
import se.ansman.dagger.auto.AutoInitialize
import se.ansman.dagger.auto.android.testing.Replaces
import javax.inject.Inject
import javax.inject.Singleton

interface Repository

@AutoInitialize
@AutoBind
@Singleton
class RealRepository @Inject constructor() : Repository

@Replaces(RealRepository::class)
@Singleton
class FakeRepository @Inject constructor() : Repository