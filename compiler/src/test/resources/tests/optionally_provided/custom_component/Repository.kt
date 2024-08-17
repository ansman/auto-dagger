package tests.optionally_provided.custom_component

import dagger.Component
import dagger.hilt.DefineComponent
import se.ansman.dagger.auto.OptionallyProvided

@Component
@Retention(AnnotationRetention.BINARY)
@DefineComponent
annotation class MyComponent

@OptionallyProvided(inComponent = MyComponent::class)
interface Repository