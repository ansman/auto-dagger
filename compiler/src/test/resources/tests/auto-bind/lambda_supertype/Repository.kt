package se.ansman.lambda_supertype

import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import javax.inject.Singleton

@AutoBind
@Singleton
class RunnableThing @Inject constructor() : () -> String {
    override fun invoke(): String = "stuff"
}