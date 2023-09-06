package se.ansman.dagger.auto.androidx.viewmodel

import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier


/**
 * A qualifier indicating that the annotated type is tied to [ViewModelComponent].
 *
 * By default the following things are automatically provided:
 * ## CoroutineScope
 * A [CoroutineScope] that is tied to the view model's lifecycle is contributed. The scope uses a [SupervisorJob] and
 * the `[Dispatchers].Main.immediate` dispatcher.
 *
 * To use it simply inject it like this:
 * ```
 * @HiltViewMode
 * class MyViewModel @Inject constructor(
 *   @ViewModelSpecific
 *   private val viewModelScope: CoroutineScope
 * ) : ViewModel()
 * ```
 */
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class ViewModelSpecific