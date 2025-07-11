package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

        AnimatorHelper.animateWelcomeSequence(welcomeText, welcomeText2);
        }
    }

class AnimatorHelper {

    public static void animateWelcomeSequence(View firstView, View secondView) {
        prepareView(firstView);
        prepareView(secondView);

        playIntro(firstView, () ->
                fadeOutAndSlide(firstView, () ->
                        playIntro(secondView, () ->
                                fadeOutAndSlide(secondView, null)
                        )
                )
        );
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
