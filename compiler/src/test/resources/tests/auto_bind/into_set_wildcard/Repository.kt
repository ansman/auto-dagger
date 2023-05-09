package tests.auto_bind.into_set_wildcard

import se.ansman.dagger.auto.AutoBindIntoSet
import se.ansman.dagger.auto.BindGenericAs
import java.util.concurrent.Callable
import javax.inject.Inject

@AutoBindIntoSet(bindGenericAs = BindGenericAs.Wildcard)
class StringProvider @Inject constructor() : Callable<String> {
    override fun call(): String = "Hello"
}

@AutoBindIntoSet(bindGenericAs = BindGenericAs.TypeAndWildcard)
class IntProvider @Inject constructor() : Callable<Int> {
    override fun call(): Int = 4711
}

@AutoBindIntoSet(bindGenericAs = BindGenericAs.Type)
class BooleanProvider @Inject constructor() : Callable<Boolean> {
    override fun call(): Boolean = true
}