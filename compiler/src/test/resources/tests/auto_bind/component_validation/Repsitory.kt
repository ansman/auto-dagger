package tests.auto_bind.component_validation

import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import javax.inject.Singleton

interface Repository

@AutoBind(inComponent = SingletonComponent::class)
@Singleton
class GlobalRepository @Inject constructor() : Repository

@AutoBind(inComponent = ActivityComponent::class)
@Singleton
class ActivityRepository @Inject constructor() : Repository

@AutoBind(inComponent = FragmentComponent::class)
@Singleton
class FragmentRepository @Inject constructor() : Repository