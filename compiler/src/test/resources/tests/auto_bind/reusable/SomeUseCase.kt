package tests.auto_bind.reusable

import dagger.Reusable
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

interface SomeUseCase

@AutoBind
@Reusable
class RealSomeUseCase @Inject constructor() : SomeUseCase