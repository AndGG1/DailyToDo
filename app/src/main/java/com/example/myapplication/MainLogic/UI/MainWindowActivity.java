package com.example.myapplication.MainLogic.UI;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.MainLogic.Data.Model.TaskItemBean;
import com.example.myapplication.MainLogic.Data.Model.Days;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Database.RegisterUsages.CryptoUtils;
import Database.RegisterUsages.FirebaseVerify;

public class MainWindowActivity extends AppCompatActivity {

    private View activeIndicator;
    private TextView titleText;
    private Button yesterdayButton, todayButton, tomorrowButton;
    private ImageButton addTaskButton;
    private ImageView settingsButton;

    private RecyclerView taskRecyclerView;
    private TaskAdapter adapter;
    private List<TaskItemBean> taskList;
    private TaskViewModel viewModel;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_main);


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {}
        });

        activeIndicator = findViewById(R.id.activeIndicator);
        titleText = findViewById(R.id.titleText);
        yesterdayButton = findViewById(R.id.yesterdayButton);
        todayButton = findViewById(R.id.todayButton);
        tomorrowButton = findViewById(R.id.tomorrowButton);
        addTaskButton = findViewById(R.id.addTaskButton);
        settingsButton = findViewById(R.id.settingsButton);

        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        viewModel = new TaskViewModel(this.getApplication());

        Days days = new Days();
        days.calculateDays();

        try {
            String encryptedUsername = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    .getString("username", "user");

            CryptoUtils.decrypt(encryptedUsername, u -> {
                String username = u.equals("-1") ? "user" : u;

                Runnable updateTitle = () -> titleText.setText(
                        (username + " - Tasks").toUpperCase() + " • " + days.getToday());

                if (username.equals("user")) {
                    FirebaseVerify.getSignedInUsername((exists, name) -> updateTitle.run());
                } else {
                    updateTitle.run();
                }

                View.OnClickListener dayClickListener = v -> {
                    if (isLoading) return;
                    setDayButtonsEnabled(false);

                    String selectedDay;
                    boolean showAddButton;

                    if (v == yesterdayButton) {
                        selectedDay = days.getYesterday();
                        showAddButton = false;
                    } else if (v == todayButton) {
                        selectedDay = days.getToday();
                        showAddButton = true;
                    } else {
                        selectedDay = days.getTomorrow();
                        showAddButton = true;
                    }

                    titleText.setText((username + " - Tasks").toUpperCase() + " • " + selectedDay);
                    activeIndicator.animate().x(v.getLeft() + ((View) v.getParent()).getLeft()).setDuration(300).start();
                    addTaskButton.setVisibility(showAddButton ? View.VISIBLE : View.GONE);

                    taskList = viewModel.getTasks(days.getCurrentDay()).getValue();
                    adapter.notifyDataSetChanged();

                    new Handler(Looper.getMainLooper()).postDelayed(() -> setDayButtonsEnabled(true), 500);
                };

                yesterdayButton.setOnClickListener(dayClickListener);
                todayButton.setOnClickListener(dayClickListener);
                tomorrowButton.setOnClickListener(dayClickListener);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        settingsButton.setOnClickListener(v -> {
            v.animate().rotationBy(360f).setDuration(400).withEndAction(() -> v.setRotation(0f)).start();
        });

        taskList = new ArrayList<>();
        adapter = new TaskAdapter(this, days);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.setAdapter(adapter);

        taskList = viewModel.getTasks(2).getValue();
        adapter.notifyDataSetChanged();

        ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder from,
                                  RecyclerView.ViewHolder to) {
                int fromPos = from.getAdapterPosition();
                int toPos = to.getAdapterPosition();
                Collections.swap(taskList, fromPos, toPos);
                adapter.notifyItemMoved(fromPos, toPos);

                taskList.get(toPos).setPos(toPos);
                taskList.get(fromPos).setPos(fromPos);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                TaskItemBean task = taskList.get(position);
                viewModel.deleteTask(task, days.getCurrentDay());
                taskList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        };
        new ItemTouchHelper(touchHelperCallback).attachToRecyclerView(taskRecyclerView);

        addTaskButton.setOnClickListener(v -> {
            TaskItemBean newTaskItem = new TaskItemBean("");
            newTaskItem.setPos(taskList.size());
            newTaskItem.setCompleted(false);
            newTaskItem.setText(newTaskItem.getText());
            newTaskItem.setImportant(false);
            viewModel.insertTask(newTaskItem, days.getCurrentDay());

            taskList.add(newTaskItem);
            adapter.notifyItemInserted(taskList.size() - 1);
            taskRecyclerView.scrollToPosition(taskList.size() - 1);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.closeDBManager();
    }

    private void setDayButtonsEnabled(boolean enabled) {
        yesterdayButton.setEnabled(enabled);
        todayButton.setEnabled(enabled);
        tomorrowButton.setEnabled(enabled);
    }
}