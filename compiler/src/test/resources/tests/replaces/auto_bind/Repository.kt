package tests.replaces.auto_bind

import dagger.hilt.android.components.FragmentComponent
import dagger.multibindings.StringKey
import se.ansman.dagger.auto.AutoBind
import se.ansman.dagger.auto.AutoBindIntoMap
import se.ansman.dagger.auto.AutoBindIntoSet
import se.ansman.dagger.auto.android.testing.Replaces
import java.io.Closeable
import javax.inject.Inject
import javax.inject.Singleton

interface Repository

@AutoBind(asTypes = [Repository::class])
@AutoBindIntoSet(asTypes = [Closeable::class])
@AutoBindIntoMap(inComponent = FragmentComponent::class, asTypes = [Runnable::class])
@StringKey("repository")
@Singleton
class RealRepository @Inject constructor() : Repository, Closeable, Runnable {
    override fun close() {}
    override fun run() {}
}

@Replaces(RealRepository::class)
@Singleton
class FakeRepository @Inject constructor() : Repository