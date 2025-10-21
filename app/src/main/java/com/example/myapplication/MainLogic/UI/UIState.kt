package com.example.myapplication.MainLogic.UI

sealed class UIState {
    object SUCCESS: UIState()
    object LOADING: UIState()
    object NOT_FOUND: UIState()
}

object StateManagement {
    val listOfStates: MutableMap<String, UIState> = HashMap<String, UIState>()
}