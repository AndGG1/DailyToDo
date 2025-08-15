package com.example.myapplication.IntroLogic;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.MainLogic.UI.MainWindowActivity;

import java.util.concurrent.atomic.AtomicBoolean;

import Database.RegisterUsages.CryptoUtils;
import Database.RegisterUsages.FirebaseVerify;

public class SignInActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_main);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing or custom logic
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);

        EditText usernameInput = findViewById(R.id.usernameInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        ImageView passwordToggle = findViewById(R.id.passwordToggle);
        Button signInButton = findViewById(R.id.signInButton);

        // Toggle password visibility
        passwordToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_visibility_off);
            } else {
                passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_visibility);
            }
            passwordInput.setSelection(passwordInput.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        // Validate inputs on button click
        signInButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            AtomicBoolean isUsernameValid = new AtomicBoolean(username.length() >= 3 && username.length() <= 12);
            boolean isPasswordValid = password.length() >= 8 && password.length() <= 12;

            if (isUsernameValid.get()) {
                FirebaseVerify.checkIfEmailExists(username, (exists) -> {
                    isUsernameValid.set(!exists);
                });
            }

            usernameInput.setBackgroundResource(
                    isUsernameValid.get() ? R.drawable.input_background : R.drawable.input_background_error);
            usernameInput.setTextColor(isUsernameValid.get() ? Color.parseColor("#4A148C") : Color.RED);


            passwordInput.setBackgroundResource(
                    isPasswordValid ? R.drawable.input_background : R.drawable.input_background_error);
            passwordInput.setTextColor(isPasswordValid ? Color.parseColor("#4A148C") : Color.RED);

            if (isUsernameValid.get() && isPasswordValid) {
                try {
                    getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            .edit()
                            .putBoolean("hasSignedInBefore", true)
                            .putString("username", CryptoUtils.encrypt(username))
                            .apply();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                FirebaseVerify.registerUser(username, password);

                Intent switchToMainWindowIntent = new Intent(this, MainWindowActivity.class);
                startActivity(switchToMainWindowIntent);
            }
        });
    }
}
