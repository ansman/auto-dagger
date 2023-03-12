package tests.auto_bind.abstract_supertype

import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import javax.inject.Singleton

interface Repository<T>

@AutoBind
@Singleton
class RealRepository @Inject constructor() : Repository<String>