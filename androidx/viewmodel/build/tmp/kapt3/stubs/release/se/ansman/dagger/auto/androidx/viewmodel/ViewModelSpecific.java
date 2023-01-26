package se.ansman.dagger.auto.androidx.viewmodel;

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
 *  @ViewModelSpecific
 *  private val viewModelScope: CoroutineScope
 * ) : ViewModel()
 * ```
 *
 * @since 1.0.0
 */
@kotlin.annotation.MustBeDocumented()
@kotlin.annotation.Retention(value = kotlin.annotation.AnnotationRetention.BINARY)
@javax.inject.Qualifier()
@java.lang.annotation.Documented()
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.CLASS)
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\n\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0000\b\u0087\u0002\u0018\u00002\u00020\u0001B\u0000\u00a8\u0006\u0002"}, d2 = {"Lse/ansman/dagger/auto/androidx/viewmodel/ViewModelSpecific;", "", "androidx-viewmodel_release"})
public abstract @interface ViewModelSpecific {
}