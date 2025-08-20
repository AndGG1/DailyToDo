package com.example.myapplication.IntroLogic.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.MainLogic.UI.MainWindowActivity;

import java.util.concurrent.atomic.AtomicBoolean;

import Database.RegisterUsages.CryptoUtils;
import Database.RegisterUsages.FirebaseVerify_KtDemo;

public class WelcomeActivity extends AppCompatActivity {

    TextView welcomeText;
    TextView welcomeText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialise widgets
        welcomeText = findViewById(R.id.welcomeText);
        welcomeText2 = findViewById(R.id.welcomeText2);

        //set start properties for welcome_widget_1
        welcomeText.setVisibility(View.VISIBLE);
        welcomeText.setAlpha(0f);
        welcomeText.setScaleX(0.5f);
        welcomeText.setScaleY(0.5f);

        //set start properties for welcome_widget_2
        welcomeText2.setVisibility(View.VISIBLE);
        welcomeText2.setAlpha(0f);
        welcomeText2.setScaleX(0.5f);
        welcomeText2.setScaleY(0.5f);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        AtomicBoolean hasSignedInBefore = new AtomicBoolean(prefs.getBoolean("hasSignedInBefore", false));

        if (!hasSignedInBefore.get()) {
            FirebaseVerify_KtDemo.getSignedUsername((exists, name) -> {
                hasSignedInBefore.set(exists);
            });
        }

        if (!hasSignedInBefore.get()) {
            AnimatorHelper.animateWelcomeSequence(welcomeText, welcomeText2, () -> {
                //After animation, we switch to new SignIn activity
                Intent switchActivityIntent = new Intent(this, SignInActivity.class);
                switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(switchActivityIntent);
            });
        } else {
            try {
                String username = prefs.getString("username", "user") + "!";
                CryptoUtils.decrypt(username, u -> {
                    welcomeText.setText("Welcome back, " + u + "!");
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            welcomeText2.setText("Let's see our schedule for today!");

            AnimatorHelper.animateWelcomeSequence(welcomeText, welcomeText2, () -> {
                Intent switchToMainWindowIntent = new Intent(this, MainWindowActivity.class);
                switchToMainWindowIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(switchToMainWindowIntent);
            });
        }
    }
}

class AnimatorHelper {

    public static void animateWelcomeSequence(View firstView, View secondView, Runnable runnable) {
        prepareView(firstView);
        prepareView(secondView);

        // ⏳ Cooldown before starting animation sequence
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            playIntro(firstView, () ->
                    fadeOutAndSlide(firstView, () ->
                            playIntro(secondView, () ->
                                    fadeOutAndSlide(secondView, runnable)
                            )
                    )
            );
        }, 750); // Delay = 500ms
    }

    private static void prepareView(View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0f);
        view.setScaleX(0.5f);
        view.setScaleY(0.5f);
    }

    private static void playIntro(View view, Runnable endAction) {
        view.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(700)
                .setInterpolator(new BounceInterpolator())
                .withEndAction(endAction)
                .start();
    }

    private static void fadeOutAndSlide(View view, Runnable endAction) {
        view.animate()
                .alpha(0f)
                .translationY(-200)
                .setDuration(1000)
                .withEndAction(endAction)
                .start();
    }
}