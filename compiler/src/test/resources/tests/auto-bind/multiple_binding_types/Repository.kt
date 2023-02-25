package se.ansman.multiple_binding_types

import dagger.MapKey
import se.ansman.dagger.auto.AutoBind
import se.ansman.dagger.auto.AutoBindIntoMap
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@MapKey
annotation class BindingKey(val name: String)

abstract class Type1
interface Type2
interface Type3

@AutoBind(asTypes = [Type1::class])
@AutoBindIntoSet(asTypes = [Type2::class])
@AutoBindIntoMap(asTypes = [Type3::class])
@BindingKey("test")
class RealType @Inject constructor() : Type1(), Type2, Type3