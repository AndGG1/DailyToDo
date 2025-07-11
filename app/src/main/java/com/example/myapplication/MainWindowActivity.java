package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainWindowActivity extends AppCompatActivity {

    private View activeIndicator;
    private TextView titleText;
    private Button yesterdayButton, todayButton, tomorrowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_main);

        activeIndicator = findViewById(R.id.activeIndicator);
        titleText = findViewById(R.id.titleText);

        yesterdayButton = findViewById(R.id.yesterdayButton);
        todayButton = findViewById(R.id.todayButton);
        tomorrowButton = findViewById(R.id.tomorrowButton);


        Days days = new Days(); days.calculateDays();
        String username = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("username", "user");
        titleText.setText((username + " - tasks").toUpperCase() + " • " + days.getToday());

        yesterdayButton.setOnClickListener(v -> {
            titleText.setText((username + " - Tasks").toUpperCase() + " • " + days.getYesterday());

            int targetX = v.getLeft() + ((View) v.getParent()).getLeft();
            activeIndicator.animate().x(targetX).setDuration(300).start();
        });

        todayButton.setOnClickListener(v -> {
            titleText.setText((username + " - Tasks").toUpperCase() + " • " + days.getToday());

            int targetX = v.getLeft() + ((View) v.getParent()).getLeft();
            activeIndicator.animate().x(targetX).setDuration(300).start();
        });

        tomorrowButton.setOnClickListener(v -> {
            titleText.setText((username + " - Tasks").toUpperCase()  + " • " + days.getTomorrow());

            int targetX = v.getLeft() + ((View) v.getParent()).getLeft();
            activeIndicator.animate().x(targetX).setDuration(300).start();
        });

        ImageView settingsButton = findViewById(R.id.settingsButton);

        settingsButton.setOnClickListener(v -> {
            v.animate()
                    .rotationBy(360f) // Full spin
                    .setDuration(400)
                    .withEndAction(() -> {
                        v.setRotation(0f); // Restore to default upright position

                        // ✨ Proceed to settings screen
                        // Example:
                        // Intent intent = new Intent(this, SettingsActivity.class);
                        // startActivity(intent);
                    })
                    .start();
        });
    }

    class Days {

        String yesterday, today, tomorrow;
        protected void calculateDays() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            today = dateFormat.format(calendar.getTime());

            calendar.add(Calendar.DAY_OF_WEEK, -1);
            yesterday = dateFormat.format(calendar.getTime());

            calendar.add(Calendar.DAY_OF_YEAR, 2);
            tomorrow = dateFormat.format(calendar.getTime());
        }

        public String getYesterday() {
            return yesterday;
        }

        public String getToday() {
            return today;
        }

        public String getTomorrow() {
            return tomorrow;
        }
    }
}