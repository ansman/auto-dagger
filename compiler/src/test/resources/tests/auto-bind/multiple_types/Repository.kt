package se.ansman.multiple_types

import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import javax.inject.Singleton

abstract class Type1
interface Type2
interface Type3

@AutoBind(asTypes = [Type1::class, Type2::class, Type3::class])
@Singleton
class RealType @Inject constructor() : Type1(), Type2, Type3