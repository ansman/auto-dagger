package se.ansman.class_supertype

import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import javax.inject.Singleton

abstract class Repository

@AutoBind
@Singleton
class RealRepository @Inject constructor() : Repository()