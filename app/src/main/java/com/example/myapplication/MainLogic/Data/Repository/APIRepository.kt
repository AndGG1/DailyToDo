package com.example.myapplication.MainLogic.Data.Repository

import AiRequest
import GenerationParameters
import android.widget.EditText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.MainLogic.Data.RetrofitIns
import com.example.myapplication.MainLogic.UI.ViewModels.EmojiViewModel
import kotlinx.coroutines.launch

const val token: String = "..." // replace with your key
val viewModel: ViewModel = EmojiViewModel()

fun getRes(input: String, holder: EditText) {
    viewModel.viewModelScope.launch {
        val requestBody = AiRequest(
            inputs = input,
            parameters = GenerationParameters(max_new_tokens = 50)
        )

        val response = RetrofitIns.service.postReq(
            model = "AndGG/my-emoji-gpt2-model",
            requestBody = requestBody,
            token = token
        )

        // Take the first generated_text
        holder.setText(response.firstOrNull()?.generated_text ?: "No output")
    }
}