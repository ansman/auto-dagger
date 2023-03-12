package tests.auto_initialize.non_initializable

import se.ansman.dagger.auto.AutoInitialize
import javax.inject.Inject
import javax.inject.Singleton

@AutoInitialize
@Singleton
class NonInitializableThing @Inject constructor()