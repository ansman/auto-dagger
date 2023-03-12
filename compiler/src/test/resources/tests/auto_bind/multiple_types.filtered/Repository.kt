package tests.auto_bind.multiple_types.filtered

import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import javax.inject.Singleton

abstract class Type1
interface Type2<T>
interface Type3

@AutoBind(asTypes = [Type2::class])
@Singleton
class RealType @Inject constructor() : Type1(), Type2<String>, Type3