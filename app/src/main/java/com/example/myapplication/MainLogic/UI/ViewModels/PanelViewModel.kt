package com.example.myapplication.MainLogic.UI.ViewModels

import Database.NotificationDB.Panel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.MainLogic.Data.Repository.PanelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PanelViewModel(private val repository: PanelRepository) : ViewModel() {
    val copyOfRepo = repository

    private val _state = MutableStateFlow<UIState3>(UIState3.STANDING)
    val state : StateFlow<UIState3> = _state

    fun upsertPanel(panel: Panel) {
        _state.value = UIState3.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            copyOfRepo.modifyPanel(panel)
            _state.value = UIState3.STANDING
        }
    }

    fun deletePanel(panel: Panel) {
        _state.value = UIState3.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            copyOfRepo.removePanel(panel)
            _state.value = UIState3.STANDING
        }
    }

    fun getPanel(id: Int) {
        _state.value = UIState3.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            copyOfRepo.getPanel(id)
            _state.value = UIState3.STANDING
        }
    }

    fun check() : Boolean {
        return state.value == UIState3.STANDING
    }
}

sealed class UIState3 {
    object STANDING : UIState3()
    object LOADING : UIState3()
}