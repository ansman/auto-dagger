package tests.auto_initialize.internal_object

import se.ansman.dagger.auto.AutoInitialize
import javax.inject.Inject
import javax.inject.Singleton

@AutoInitialize
@Singleton
internal class InternalObject @Inject constructor()