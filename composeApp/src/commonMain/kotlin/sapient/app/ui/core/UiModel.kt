package sapient.app.ui.core

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import moe.tlaster.precompose.viewmodel.ViewModel

abstract class UiModel<T: UiState>(
    initialState: T
) : ViewModel() {
    protected val _state = mutableStateOf(initialState)
    protected var sv
        get() = _state.value
        set(value) { _state.value = value }
    val state: State<T> = _state
}