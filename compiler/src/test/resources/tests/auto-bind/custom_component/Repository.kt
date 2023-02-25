package se.ansman.custom_component

import dagger.Component
import dagger.hilt.DefineComponent
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import javax.inject.Singleton

@Component
@Retention(AnnotationRetention.SOURCE)
@DefineComponent
annotation class MyComponent

interface Repository

@AutoBind(inComponent = MyComponent::class)
@Singleton
class RealRepository @Inject constructor() : Repository