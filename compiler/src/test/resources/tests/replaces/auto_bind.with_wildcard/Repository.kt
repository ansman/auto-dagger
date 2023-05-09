package tests.replaces.auto_bind.with_wildcard

import dagger.hilt.android.components.FragmentComponent
import dagger.multibindings.StringKey
import se.ansman.dagger.auto.AutoBind
import se.ansman.dagger.auto.AutoBindIntoMap
import se.ansman.dagger.auto.AutoBindIntoSet
import se.ansman.dagger.auto.BindGenericAs
import se.ansman.dagger.auto.android.testing.Replaces
import java.util.concurrent.Callable
import javax.inject.Inject
import javax.inject.Singleton

interface Repository

@AutoBind(asTypes = [Repository::class])
@AutoBindIntoSet(asTypes = [Callable::class], bindGenericAs = BindGenericAs.Wildcard)
@AutoBindIntoMap(
    inComponent = FragmentComponent::class,
    asTypes = [Callable::class],
    bindGenericAs = BindGenericAs.TypeAndWildcard
)
@StringKey("repository")
@Singleton
class RealRepository @Inject constructor() : Repository, Callable<String> {
    override fun call(): String = "Hello"
}

@Replaces(RealRepository::class)
@Singleton
class FakeRepository @Inject constructor() : Repository