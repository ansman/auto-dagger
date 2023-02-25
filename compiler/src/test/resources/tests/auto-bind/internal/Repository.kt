package se.ansman.internal

import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import javax.inject.Singleton

interface Repository

@AutoBind
@Singleton
internal class RealRepository @Inject constructor() : Repository