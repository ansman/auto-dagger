package tests.auto_bind.into_set_with_qualifier_2.lifecycle.di

import tests.auto_bind.into_set_with_qualifier_2.lifecycle.AppLifecycleTask
import javax.inject.Qualifier

@Qualifier public annotation class LifecycleTask(val lifecycleStep: AppLifecycleTask.LifecycleStep)