package se.ansman.dagger.auto

import se.ansman.dagger.auto.android.testing.Replaces
import javax.inject.Inject
import javax.inject.Singleton

@Replaces(RealReplaceableRepository::class)
@Singleton
class FakeReplaceableRepository @Inject constructor() : ReplaceableRepository