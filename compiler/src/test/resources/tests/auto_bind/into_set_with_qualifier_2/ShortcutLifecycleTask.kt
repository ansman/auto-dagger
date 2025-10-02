package tests.auto_bind.into_set_with_qualifier_2

import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject
import tests.auto_bind.into_set_with_qualifier_2.lifecycle.AppLifecycleTask
import tests.auto_bind.into_set_with_qualifier_2.lifecycle.di.LifecycleTask

@AutoBindIntoSet
@LifecycleTask(lifecycleStep = AppLifecycleTask.LifecycleStep.AppForegrounded)
class ShortcutLifecycleTask @Inject constructor() : AppLifecycleTask()

