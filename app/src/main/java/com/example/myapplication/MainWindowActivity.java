package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainWindowActivity extends AppCompatActivity {

    private View activeIndicator;
    private TextView titleText;
    private Button yesterdayButton, todayButton, tomorrowButton;
    private ImageButton addTaskButton;
    private ImageView settingsButton;

    private RecyclerView taskRecyclerView;
    private TaskAdapter adapter;
    private List<TaskItem> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_main);

        activeIndicator = findViewById(R.id.activeIndicator);
        titleText = findViewById(R.id.titleText);
        yesterdayButton = findViewById(R.id.yesterdayButton);
        todayButton = findViewById(R.id.todayButton);
        tomorrowButton = findViewById(R.id.tomorrowButton);
        addTaskButton = findViewById(R.id.addTaskButton);
        settingsButton = findViewById(R.id.settingsButton);
        taskRecyclerView = findViewById(R.id.taskRecyclerView);

        Days days = new Days();
        days.calculateDays();

        String username = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("username", "user");

        titleText.setText((username + " - tasks").toUpperCase() + " • " + days.getToday());

        yesterdayButton.setOnClickListener(v -> {
            titleText.setText((username + " - Tasks").toUpperCase() + " • " + days.getYesterday());
            int targetX = v.getLeft() + ((View) v.getParent()).getLeft();
            activeIndicator.animate().x(targetX).setDuration(300).start();
            addTaskButton.setVisibility(View.GONE);
        });

        todayButton.setOnClickListener(v -> {
            titleText.setText((username + " - Tasks").toUpperCase() + " • " + days.getToday());
            int targetX = v.getLeft() + ((View) v.getParent()).getLeft();
            activeIndicator.animate().x(targetX).setDuration(300).start();
            addTaskButton.setVisibility(View.VISIBLE);
        });

        tomorrowButton.setOnClickListener(v -> {
            titleText.setText((username + " - Tasks").toUpperCase() + " • " + days.getTomorrow());
            int targetX = v.getLeft() + ((View) v.getParent()).getLeft();
            activeIndicator.animate().x(targetX).setDuration(300).start();
            addTaskButton.setVisibility(View.VISIBLE);
        });

        settingsButton.setOnClickListener(v -> {
            v.animate()
                    .rotationBy(360f)
                    .setDuration(400)
                    .withEndAction(() -> v.setRotation(0f))
                    .start();
            // Optional: startActivity(new Intent(this, SettingsActivity.class));
        });

        // Initialize RecyclerView and Adapter
        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList);

        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.setAdapter(adapter);

        // Drag-and-drop logic
        ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder from,
                                  RecyclerView.ViewHolder to) {
                int fromPos = from.getAdapterPosition();
                int toPos = to.getAdapterPosition();
                Collections.swap(taskList, fromPos, toPos);
                adapter.notifyItemMoved(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                taskList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        };
        new ItemTouchHelper(touchHelperCallback).attachToRecyclerView(taskRecyclerView);

        // Add new task
        addTaskButton.setOnClickListener(v -> {
            taskList.add(new TaskItem(""));
            adapter.notifyItemInserted(taskList.size() - 1);
            taskRecyclerView.scrollToPosition(taskList.size() - 1);
        });
    }

    // Task model
    public static class TaskItem {
        private String text;
        private boolean isCompleted;

        public TaskItem(String text) {
            this.text = text;
            this.isCompleted = false;
        }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public boolean isCompleted() { return isCompleted; }
        public void setCompleted(boolean completed) { this.isCompleted = completed; }
    }

    // Day navigation helper
    static class Days {
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
        public String getYesterday() { return yesterday; }
        public String getToday() { return today; }
        public String getTomorrow() { return tomorrow; }
    }

    // Adapter for tasks
    public static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
        private final List<TaskItem> taskList;

        public TaskAdapter(List<TaskItem> taskList) {
            this.taskList = taskList;
        }

        public static class TaskViewHolder extends RecyclerView.ViewHolder {
            EditText taskInput;
            CheckBox taskCheckbox;

            public TaskViewHolder(View itemView) {
                super(itemView);
                taskInput = itemView.findViewById(R.id.taskInput);
                taskCheckbox = itemView.findViewById(R.id.taskCheckbox);
            }
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_item, parent, false);
            return new TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            TaskItem item = taskList.get(position);

            // Remove all listeners to prevent unexpected reuse behavior
            holder.taskCheckbox.setOnCheckedChangeListener(null);
            holder.taskInput.setText(""); // Clear before setting

            // Set current state
            holder.taskInput.setText(item.getText());
            holder.taskCheckbox.setChecked(item.isCompleted());

            // Listener for text changes
            holder.taskInput.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    item.setText(holder.taskInput.getText().toString());
                }
            });

            // Listener for checkbox state
            holder.taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setCompleted(isChecked);
            });
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }
    }
}
