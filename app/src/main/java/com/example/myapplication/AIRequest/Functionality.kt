package com.example.myapplication.AIRequest

import android.util.Log
import android.widget.EditText
import androidx.lifecycle.viewModelScope
import com.example.myapplication.MainLogic.UI.ViewModels.EmojiViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.function.Consumer

fun aiRequest(input: String, viewModel: EmojiViewModel, taskInput: EditText, func: Consumer<Object>) {
    if (viewModel.state.value == UIState.LOADING) return

    viewModel.viewModelScope.launch(Dispatchers.IO) {
        viewModel.setLoading()
        try {
            val resp = RetrofitIns.instance
                .queryModel(mapOf("inputs" to input)).execute()

            val s: String = taskInput.text.toString()
            taskInput.setText(resp.body()?.get("generated_text").toString())
            viewModel.setSuccess()

            if (taskInput.text.toString().equals("null")) taskInput.setText(s)
            func.accept(Object())
        } catch (e: Exception) {
            Log.d("test+nga", "failed")
            viewModel.setNotFound()
            func.accept(Object())
        }
    }
}