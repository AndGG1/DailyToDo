package com.example.myapplication.MainLogic.UI.ViewModels

import androidx.lifecycle.ViewModel
import com.example.myapplication.MainLogic.UI.StateManagement
import com.example.myapplication.MainLogic.UI.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EmojiViewModel() : ViewModel() {
    private val _state = MutableStateFlow<MutableMap<String, UIState>>(StateManagement.listOfStates)

    //State deciding the "accessory" added to the
    // right of the task-like sentence on each separate task fragment
    val state: StateFlow<Map<String, UIState>> = _state.asStateFlow()

    fun addNewState(uid: String) {
        _state.value.put(uid, UIState.NOT_FOUND)
    }

    fun removeState(uid: String) {
        _state.value.remove(uid)
    }

    fun moveTwoStates() {
        //TODO
    }

    fun setSuccess(uid: String) {
        _state.value.put(uid, UIState.SUCCESS)
    }

    fun setLoading(uid: String) {
        _state.value.put(uid, UIState.LOADING)
    }

    fun setNotFound(uid: String) {
        _state.value.put(uid, UIState.NOT_FOUND)
    }
}
