package com.example.myapplication.AIRequest

sealed class UIState {
    object SUCCESS: UIState()
    object LOADING: UIState()
    object NOT_FOUND: UIState()
}