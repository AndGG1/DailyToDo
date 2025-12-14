package com.example.myapplication.IntroLogic.UI.RegisterUsages

import Database.RegisterUsages.IsValidCallback
import Database.RegisterUsages.encrypt
import Database.RegisterUsages.registerUser
import SignInTemplate
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.core.content.edit
import com.example.myapplication.MainLogic.UI.MainWindowActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ComposeTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                SignInTemplate(context = this)
            }
        }
    }
}

fun registerUserComp(username: String, password: String, context: Context) {
    runBlocking {
        try {
            context.getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .edit(commit = true) {
                    putBoolean("hasSignedInBefore", true)
                        .putString(
                            "username",
                            withContext(Dispatchers.Default) {
                                encrypt(username)
                            }
                        )
                }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }


    registerUser(username, password, object : IsValidCallback {
        override fun onRes(isValid: Boolean, uid: String?) {
            if (isValid) {
                context.startActivity(Intent(context, MainWindowActivity::class.java))
            }
        }
    })
}