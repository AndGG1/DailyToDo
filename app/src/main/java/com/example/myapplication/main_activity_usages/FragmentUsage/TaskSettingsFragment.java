package com.example.myapplication.main_activity_usages.FragmentUsage;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

import java.util.Calendar;

public class TaskSettingsFragment extends Fragment {

    private TextView timeLabel;
    private Switch notificationSwitch, repeatSwitch;
    private Calendar selectedTime;

    public TaskSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_task_settings_panel, container, false);

        timeLabel = view.findViewById(R.id.timeLabel);
        notificationSwitch = view.findViewById(R.id.notificationSwitch);
        repeatSwitch = view.findViewById(R.id.repeatSwitch);

        selectedTime = Calendar.getInstance();

        timeLabel.setOnClickListener(v -> {
            int hour = selectedTime.get(Calendar.HOUR_OF_DAY);
            int minutes = selectedTime.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(
                    getContext(),
                    (timePicker, selectedHour, selectedMinute) -> {
                        selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        selectedTime.set(Calendar.MINUTE, selectedMinute);
                        timeLabel.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    },
                    hour, minutes, true
            );
            dialog.show();
        });

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(getContext(), isChecked ? "Notification ON" : "Notification OFF", Toast.LENGTH_SHORT).show();
        });

        repeatSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(getContext(), isChecked ? "Repeating ON" : "Repeating OFF", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
