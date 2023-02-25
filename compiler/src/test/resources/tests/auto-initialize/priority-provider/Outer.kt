package se.ansman.priority.provider

import se.ansman.dagger.auto.AutoInitialize
import javax.inject.Inject
import javax.inject.Singleton

object Outer {
    @AutoInitialize(priority = 4711)
    @Singleton
    class SomeThing @Inject constructor()
}