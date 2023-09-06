package se.ansman.dagger.auto.androidx.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@HiltViewModel
class ExampleViewModel @Inject constructor(
    @ViewModelSpecific
    private val viewModelScope: CoroutineScope
) : ViewModel()