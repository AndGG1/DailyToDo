package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import Database.FirebaseVerify;

public class SignInActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_main);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {}
        });

        EditText usernameInput = findViewById(R.id.usernameInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        ImageView passwordToggle = findViewById(R.id.passwordToggle);
        Button signInButton = findViewById(R.id.signInButton);

        passwordToggle.setOnClickListener(v -> {
            passwordInput.setInputType(isPasswordVisible
                    ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    : InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordToggle.setImageResource(isPasswordVisible
                    ? R.drawable.ic_visibility_off
                    : R.drawable.ic_visibility);
            passwordInput.setSelection(passwordInput.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        signInButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            boolean isUsernameValid = username.length() >= 3 && username.length() <= 12;
            boolean isPasswordValid = password.length() >= 8 && password.length() <= 12;

            usernameInput.setBackgroundResource(isUsernameValid ? R.drawable.input_background : R.drawable.input_background_error);
            usernameInput.setTextColor(isUsernameValid ? Color.parseColor("#4A148C") : Color.RED);

            passwordInput.setBackgroundResource(isPasswordValid ? R.drawable.input_background : R.drawable.input_background_error);
            passwordInput.setTextColor(isPasswordValid ? Color.parseColor("#4A148C") : Color.RED);

            if (isUsernameValid && isPasswordValid) {
                FirebaseVerify.registerUser(username, password, this);

                Intent intent = new Intent(this, MainWindowActivity.class);
                startActivity(intent);
            }
        });
    }
}