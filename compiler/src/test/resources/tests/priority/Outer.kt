package se.ansman.priority

import se.ansman.deager.Eager
import javax.inject.Inject
import javax.inject.Singleton

object Outer {
    @Eager(priority = 4711)
    @Singleton
    class SomeThing @Inject constructor()
}