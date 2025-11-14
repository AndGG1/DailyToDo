package com.example.myapplication.IntroLogic.UI.RegisterUsages

import Database.RegisterUsages.IsValidCallback
import Database.RegisterUsages.encrypt
import Database.RegisterUsages.registerUser
import SignInTemplate
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.core.content.edit
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

fun registerUserComp(username: String, password: String, context: Context) : Boolean {
    var success = true
    registerUser(username, password, object : IsValidCallback {
        override fun onRes(isValid: Boolean, uid: String?) {
            if (isValid) {
                runBlocking {
                    try {
                        val encryptedUsername = withContext(Dispatchers.Default) {
                            encrypt(username)
                        }

                        context.getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            .edit {
                                putBoolean("hasSignedInBefore", true)
                                    .putString("username", encryptedUsername)
                            }
                    } catch (e: Exception) {
                        success = false
                    }
                }
            } else success = false
        }
    })
    return success
}