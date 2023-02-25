package se.ansman.simple

import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import javax.inject.Singleton

interface Repository

@AutoBind
@Singleton
class RealRepository @Inject constructor() : Repository