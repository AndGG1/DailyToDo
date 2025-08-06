package com.example.myapplication.main_activity_usages;

import android.database.Cursor;
import android.os.Bundle;
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
import com.example.myapplication.main_activity_usages.adapter.TaskAdapter;
import com.example.myapplication.main_activity_usages.bean_data_usage.TaskItemBean;
import com.example.myapplication.main_activity_usages.time_class.Days;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Database.DatabaseUsages.TasksDB.DatabaseManager;
import Database.RegisterUsages.CryptoUtils;

public class MainWindowActivity extends AppCompatActivity {

    private View activeIndicator;
    private TextView titleText;
    private Button yesterdayButton, todayButton, tomorrowButton;
    private ImageButton addTaskButton;
    private ImageView settingsButton;

    private RecyclerView taskRecyclerView;
    private TaskAdapter adapter;
    private List<TaskItemBean> taskList;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_main);

        dbManager = new DatabaseManager(this).open();

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

        Days days = new Days();
        days.calculateDays();

        String username;
        try {
            username = CryptoUtils.decrypt(getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    .getString("username", "user"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        titleText.setText((username + " - tasks").toUpperCase() + " • " + days.getToday());

        yesterdayButton.setOnClickListener(v -> {
            titleText.setText((username + " - Tasks").toUpperCase() + " • " + days.getYesterday());
            activeIndicator.animate().x(v.getLeft() + ((View) v.getParent()).getLeft()).setDuration(300).start();
            addTaskButton.setVisibility(View.GONE);
        });

        todayButton.setOnClickListener(v -> {
            titleText.setText((username + " - Tasks").toUpperCase() + " • " + days.getToday());
            activeIndicator.animate().x(v.getLeft() + ((View) v.getParent()).getLeft()).setDuration(300).start();
            addTaskButton.setVisibility(View.VISIBLE);
        });

        tomorrowButton.setOnClickListener(v -> {
            titleText.setText((username + " - Tasks").toUpperCase() + " • " + days.getTomorrow());
            activeIndicator.animate().x(v.getLeft() + ((View) v.getParent()).getLeft()).setDuration(300).start();
            addTaskButton.setVisibility(View.VISIBLE);
        });

        settingsButton.setOnClickListener(v -> {
            v.animate().rotationBy(360f).setDuration(400).withEndAction(() -> v.setRotation(0f)).start();
        });

        taskList = new ArrayList<>();

        // Load tasks from database
        Cursor cursor = dbManager.fetch();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                String text = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String checked = cursor.getString(cursor.getColumnIndexOrThrow("checked"));

                TaskItemBean task = new TaskItemBean(text);
                task.setDbId(id);
                task.setImportant("important".equals(desc));
                task.setCompleted("1".equals(checked));
                task.setPos(taskList.size());

                taskList.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new TaskAdapter(this, taskList, dbManager);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.setAdapter(adapter);

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
                dbManager.delete(task.getDbId());
                taskList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        };
        new ItemTouchHelper(touchHelperCallback).attachToRecyclerView(taskRecyclerView);

        addTaskButton.setOnClickListener(v -> {
            TaskItemBean newTaskItem = new TaskItemBean("");
            newTaskItem.setPos(taskList.size());
            long id = dbManager.insert(newTaskItem.getText(), "regular", "0");
            newTaskItem.setDbId(id);

            taskList.add(newTaskItem);
            adapter.notifyItemInserted(taskList.size() - 1);
            taskRecyclerView.scrollToPosition(taskList.size() - 1);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}