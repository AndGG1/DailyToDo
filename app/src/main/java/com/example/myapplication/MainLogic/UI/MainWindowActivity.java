package com.example.myapplication.MainLogic.UI;

import static com.example.myapplication.AIRequest.FunctionalityKt.aiRequest;
import static Database.RegisterUsages.CyptoUtils_KtDemoKt.decrypt;
import static Database.RegisterUsages.FirebaseVerify_KtDemoKt.getSignedUsername;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainLogic.Data.Repository.TaskRepository;
import com.example.myapplication.MainLogic.UI.ViewModels.EmojiViewModel;
import com.example.myapplication.MainLogic.Data.Model.TaskItemBean;
import com.example.myapplication.MainLogic.Data.Model.Days;
import com.example.myapplication.MainLogic.UI.ViewModels.TaskViewModel;
import com.example.myapplication.databinding.ActivityThirdMainBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import Database.BatchWorker.NotificationWorker;

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
    private ActivityThirdMainBinding binding;
    private final Days days = new Days();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThirdMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activeIndicator = binding.activeIndicator;
        titleText = binding.titleText;
        yesterdayButton = binding.yesterdayButton;
        todayButton     = binding.todayButton;
        tomorrowButton  = binding.tomorrowButton;
        addTaskButton   = binding.addTaskButton;
        settingsButton  = binding.settingsButton;
        taskRecyclerView = binding.taskRecyclerView;

        days.calculateDays();
        taskList = new ArrayList<>();
        adapter = new TaskAdapter(new AdapterListener(), days);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.setAdapter(adapter);
        viewModel = new TaskViewModel(new TaskRepository(this));
        emojiViewModel = new EmojiViewModel();

        setup();

        viewModel.getTasks().observe(this, tasks -> {
                taskList = tasks;
                adapter.sortTasks();
            }
        );

        try {
            String u = decrypt(getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    .getString("username", "user"));
                String username = u.equals("-1") ? "user" : u;

                if (username.equals("user")) {
                    getSignedUsername((exists, name) -> {
                        titleText.setText(("Hello, " + username) + " • " + days.getToday());
                    });
                } else
                    titleText.setText(("Hello, " + username) + " • " + days.getToday());

                yesterdayButton.setOnClickListener(v -> {
                    if (isLoading) return;
                    setDayButtonsEnabled(false);

                    String selectedDay = days.getYesterday();
                    titleText.setText(("Hello, " + username) + " • " + selectedDay);
                    activeIndicator.animate().x(v.getLeft() + ((View) v.getParent()).getLeft()).setDuration(300).start();
                    addTaskButton.setVisibility(View.GONE); // Yesterday shouldn't allow adding tasks

                    viewModel.loadTasks(days.getCurrentDay());
                    adapter.sortTasks();
                    adapter.notifyDataSetChanged();

                    new Handler(Looper.getMainLooper()).postDelayed(() -> setDayButtonsEnabled(true), 500);
                });

                todayButton.setOnClickListener(v -> {
                    if (isLoading) return;
                    setDayButtonsEnabled(false);

                    String selectedDay = days.getToday();
                    titleText.setText(("Hello, " + username) + " • " + selectedDay);
                    activeIndicator.animate().x(v.getLeft() + ((View) v.getParent()).getLeft()).setDuration(300).start();
                    addTaskButton.setVisibility(View.VISIBLE);

                    viewModel.loadTasks(days.getCurrentDay());
                    adapter.sortTasks();
                    adapter.notifyDataSetChanged();

                    new Handler(Looper.getMainLooper()).postDelayed(() -> setDayButtonsEnabled(true), 500);
                });

                tomorrowButton.setOnClickListener(v -> {
                    if (isLoading) return;
                    setDayButtonsEnabled(false);

                    String selectedDay = days.getTomorrow();
                    titleText.setText(("Hello, " + username + " • " + selectedDay));
                    activeIndicator.animate().x(v.getLeft() + ((View) v.getParent()).getLeft()).setDuration(300).start();
                    addTaskButton.setVisibility(View.VISIBLE);

                    viewModel.loadTasks(days.getCurrentDay());
                    adapter.sortTasks();
                    adapter.notifyDataSetChanged();

                    new Handler(Looper.getMainLooper()).postDelayed(() -> setDayButtonsEnabled(true), 500);
                });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        settingsButton.setOnClickListener(v -> {
            v.animate().rotationBy(360f).setDuration(400).withEndAction(() -> v.setRotation(0f)).start();
        });

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

                viewModel.getTasks().getValue().get(toPos).setPos(toPos);
                viewModel.getTasks().getValue().get(fromPos).setPos(fromPos);
                viewModel.updateTask(taskList.get(toPos), days.getCurrentDay());
                viewModel.updateTask(taskList.get(fromPos), days.getCurrentDay());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (viewModel.check()) {
                    int position = viewHolder.getAdapterPosition();
                    TaskItemBean task = taskList.get(position);
                    viewModel.deleteTask(task, days.getCurrentDay());
                    adapter.notifyItemRemoved(position);
                }
            }
        };
        new ItemTouchHelper(touchHelperCallback).attachToRecyclerView(taskRecyclerView);

        addTaskButton.setOnClickListener(v -> {
            if (viewModel.check()) {
                TaskItemBean newTaskItem = new TaskItemBean("");
                newTaskItem.setPos(taskList.size());
                newTaskItem.setImportant(false);
                newTaskItem.setCompleted(false);
                newTaskItem.setText(newTaskItem.getText());
                viewModel.insertTask(newTaskItem, days.getCurrentDay());

                adapter.notifyItemInserted(taskList.size());
                taskRecyclerView.scrollToPosition(taskList.size());
            }
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

    private void setup() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });

        NotificationWorker.runBatchNotif(viewModel.getCopyOfRepo().getDbManager());

        viewModel.loadTasks(days.getCurrentDay());
        adapter.sortTasks();
        adapter.notifyDataSetChanged();
    }

    class AdapterListener {
        void updateT(TaskItemBean task, int day) {
            viewModel.updateTask(task, day);
        }

        void requestEmoji(String input, EditText taskInput, Consumer<Object> func) {
            aiRequest(input, emojiViewModel, taskInput, func);
        }

        List<TaskItemBean> getTaskList() {
            return taskList;
        }
    }
}