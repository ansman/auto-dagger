package se.ansman.dagger.auto.androidx.viewmodel

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.ViewModelLifecycle
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.RetainedLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * A Dagger module which provides a [CoroutineScope] that is tied to the lifecycle of a [ViewModel].
 *
 * The provided scope is qualified with [ViewModelSpecific] and will be canceled when the viewmodel is cleared and it
 * ses a [SupervisorJob] and the `[Dispatchers].Main.immediate` dispatcher.
 *
 * To inject:
 * ```
 * @HiltViewModel
 * class MyViewModel @Inject constructor(
 *   @ViewModelSpecific
 *   private val viewModelScope: CoroutineScope
 * ) : ViewModel()
 * ```
 *
 * @since 1.0.0
 */
@Module
@InstallIn(ViewModelComponent::class)
object AutoDaggerViewModelCoroutineScopeComponent {
    @Provides
    @ViewModelSpecific
    fun provideViewModelScope(lifecycle: ViewModelLifecycle): CoroutineScope =
        ClearableCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
            .also(lifecycle::addOnClearedListener)
}

private class ClearableCoroutineScope(
    override val coroutineContext: CoroutineContext
) : CoroutineScope, RetainedLifecycle.OnClearedListener {

    override fun onCleared() {
        coroutineContext.cancel()
    }
}