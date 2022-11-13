package se.ansman.non_initializable

import se.ansman.deager.Eager
import javax.inject.Inject
import javax.inject.Singleton

@Eager
@Singleton
class NonInitializableThing @Inject constructor()