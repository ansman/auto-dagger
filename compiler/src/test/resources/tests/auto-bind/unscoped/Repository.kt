package se.ansman.unscoped

import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

interface Repository

@AutoBind
class RealRepository @Inject constructor() : Repository