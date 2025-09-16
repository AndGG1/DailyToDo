package com.example.myapplication.IntroLogic.UI.RegisterUsages

import SignInTemplate
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme

class ComposeTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                SignInTemplate {
                    a: String, b: String -> {}
                }
            }
        }
    }
}