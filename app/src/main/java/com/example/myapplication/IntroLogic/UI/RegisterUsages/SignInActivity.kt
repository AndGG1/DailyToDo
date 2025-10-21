//package com.example.myapplication.IntroLogic.UI.RegisterUsages
//
//import Database.RegisterUsages.IsValidCallback
//import Database.RegisterUsages.encrypt
//import Database.RegisterUsages.registerUser
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.graphics.Color
//import android.os.Bundle
//import android.text.InputType
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageView
//import androidx.activity.OnBackPressedCallback
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.edit
//import androidx.core.graphics.toColorInt
//import com.example.myapplication.MainLogic.UI.MainWindowActivity
//import com.example.myapplication.R
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.runBlocking
//import kotlinx.coroutines.withContext
//
//class SignInActivity : AppCompatActivity() {
//    var isPasswordVisible = false
//
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_second_main_old)
//
//        val callback: OnBackPressedCallback = object: OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                //do nothing
//            }
//        }
//        onBackPressedDispatcher.addCallback(this, callback)
//
//        val usernameInput = findViewById<EditText>(R.id.usernameInput)
//        val passwordInput = findViewById<EditText>(R.id.passwordInput)
//        val passwordToggle = findViewById<ImageView>(R.id.passwordToggle)
//        val signInButton = findViewById<Button>(R.id.signInButton)
//
//        passwordToggle.setOnClickListener({v ->
//            if (isPasswordVisible) {
//                passwordInput.inputType =
//                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
//                passwordToggle.setImageResource(R.drawable.ic_visibility_off)
//            } else {
//                passwordInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
//                passwordToggle.setImageResource(R.drawable.ic_visibility)
//            }
//            passwordInput.setSelection(passwordInput.text.length)
//            isPasswordVisible = !isPasswordVisible
//        })
//
//        signInButton.setOnClickListener({v ->
//            var username = usernameInput.text.toString().trim()
//            val password = passwordInput.text.toString().trim()
//
//            var isUsernameValid = username.length in 2..20
//            val isPasswordValid = password.length in 4..12
//
//            checkValidState(usernameInput, isUsernameValid, passwordInput, isPasswordValid)
//
//            if (isUsernameValid && isPasswordValid) {
//                runBlocking {
//                    launch {
//                        try {
//                            getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
//                                .edit {
//                                    putBoolean("hasSignedInBefore", true)
//                                        .putString(
//                                            "username",
//                                            withContext(Dispatchers.Default) {
//                                                encrypt(username)
//                                            }
//                                        )
//                                }
//                        } catch (e: Exception) {
//                            throw RuntimeException(e)
//                        }
//                    }
//                }
//
//                registerUser(username, password, object : IsValidCallback {
//                    override fun onRes(isValid: Boolean, uid: String?) {
//                        if (!isValid) {
//                            checkValidState(usernameInput, false, passwordInput, false)
//                        } else {
//                            startActivity(Intent(v.context, MainWindowActivity::class.java));
//                        }
//                    }
//                })
//            }
//        })
//    }
//
//    private fun checkValidState(usernameInput : EditText, isUsernameValid: Boolean,
//                                passwordInput: EditText, isPasswordValid: Boolean) {
//
//        usernameInput.setBackgroundResource(
//            if (isUsernameValid) R.drawable.input_background else R.drawable.input_background_error
//        )
//        usernameInput.setTextColor(if (isUsernameValid) "#4A148C".toColorInt() else Color.RED)
//
//        passwordInput.setBackgroundResource(
//            if (isPasswordValid) R.drawable.input_background else R.drawable.input_background_error
//        )
//        passwordInput.setTextColor(if (isPasswordValid) "#4A148C".toColorInt() else Color.RED)
//    }
//}