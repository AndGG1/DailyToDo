package com.example.myapplication.MainLogic.UI;

import static Database.RegisterUsages.CyptoUtils_KtDemoKt.decrypt;
import static Database.RegisterUsages.FirebaseVerify_KtDemoKt.getSignedUsername;

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

import com.example.myapplication.MainLogic.UI.ViewModels.EmojiViewModel;
import com.example.myapplication.MainLogic.UI.ViewModels.TaskViewModel;
import com.example.myapplication.R;
import com.example.myapplication.MainLogic.Data.Model.TaskItemBean;
import com.example.myapplication.MainLogic.Data.Model.Days;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private EmojiViewModel emojiViewModel;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_main);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });

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
        taskList = new ArrayList<>();
        adapter = new TaskAdapter(this, taskList, days);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.setAdapter(adapter);
        viewModel = adapter.getViewModel();
        emojiViewModel = adapter.getEmojiViewModel();


        try {
//            String u = decrypt(getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
//                    .getString("username", "user"));
            String u = "Andrei";
                String username = u.equals("-1") ? "user" : u;

                if (username.equals("user")) {
                    getSignedUsername((exists, name) -> {
                        titleText.setText((username + " - Tasks").toUpperCase() + " • " + days.getToday());
                    });
                } else
                    titleText.setText((username + " - Tasks").toUpperCase() + " • " + days.getToday());

                yesterdayButton.setOnClickListener(v -> {
                    if (isLoading) return;
                    setDayButtonsEnabled(false);

                    String selectedDay = days.getYesterday();
                    titleText.setText((username + " - Tasks").toUpperCase() + " • " + selectedDay);
                    activeIndicator.animate().x(v.getLeft() + ((View) v.getParent()).getLeft()).setDuration(300).start();
                    addTaskButton.setVisibility(View.GONE); // Yesterday shouldn't allow adding tasks

                    viewModel.loadTasks(taskList, days.getCurrentDay(), adapter);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> setDayButtonsEnabled(true), 500);
                });

                todayButton.setOnClickListener(v -> {
                    if (isLoading) return;
                    setDayButtonsEnabled(false);

                    String selectedDay = days.getToday();
                    titleText.setText((username + " - Tasks").toUpperCase() + " • " + selectedDay);
                    activeIndicator.animate().x(v.getLeft() + ((View) v.getParent()).getLeft()).setDuration(300).start();
                    addTaskButton.setVisibility(View.VISIBLE);

                    viewModel.loadTasks(taskList, days.getCurrentDay(), adapter);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> setDayButtonsEnabled(true), 500);
                });

                tomorrowButton.setOnClickListener(v -> {
                    if (isLoading) return;
                    setDayButtonsEnabled(false);

                    String selectedDay = days.getTomorrow();
                    titleText.setText((username + " - Tasks").toUpperCase() + " • " + selectedDay);
                    activeIndicator.animate().x(v.getLeft() + ((View) v.getParent()).getLeft()).setDuration(300).start();
                    addTaskButton.setVisibility(View.VISIBLE);

                    viewModel.loadTasks(taskList, days.getCurrentDay(), adapter);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> setDayButtonsEnabled(true), 500);
                });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        settingsButton.setOnClickListener(v -> {
            v.animate().rotationBy(360f).setDuration(400).withEndAction(() -> v.setRotation(0f)).start();
        });

        viewModel.loadTasks(taskList, days.getCurrentDay(), adapter);

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

                emojiViewModel.removeState(task.getTask_id());
            }
        };
        new ItemTouchHelper(touchHelperCallback).attachToRecyclerView(taskRecyclerView);

        addTaskButton.setOnClickListener(v -> {
            TaskItemBean newTaskItem = new TaskItemBean("");
            newTaskItem.setPos(taskList.size());
            newTaskItem.setImportant(false);
            newTaskItem.setCompleted(false);
            newTaskItem.setText(newTaskItem.getText());
            newTaskItem.setDbId(viewModel.insertTask(newTaskItem, days.getCurrentDay()));

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
