package com.example.myapplication.MainLogic.UI.ViewModels

import androidx.lifecycle.ViewModel
import com.example.myapplication.AIRequest.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EmojiViewModel() : ViewModel() {
    private val _state = MutableStateFlow<UIState>(UIState.NOT_FOUND)

    //State deciding the "accessory" added to the
    // right of the task-like sentence on each separate task fragment
    val state: StateFlow<UIState> = _state.asStateFlow()

    fun setSuccess() {
        _state.value = UIState.SUCCESS
    }

    fun setLoading() {
        _state.value = UIState.LOADING
    }

    fun setNotFound() {
        _state.value = UIState.NOT_FOUND
    }
}
