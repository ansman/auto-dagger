package tests.auto_bind.into_set_with_qualifier

import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

interface Interceptor

@AutoBindIntoSet
@Named("api")
@Singleton
class AuthInterceptor @Inject constructor() : Interceptor