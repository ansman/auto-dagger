package se.ansman.priority.binding

import se.ansman.dagger.auto.AutoInitialize
import se.ansman.dagger.auto.Initializable
import javax.inject.Inject
import javax.inject.Singleton

object Outer {
    @AutoInitialize(priority = 4711)
    @Singleton
    class SomeThing @Inject constructor() : Initializable {
        override fun initialize() {}
    }
}