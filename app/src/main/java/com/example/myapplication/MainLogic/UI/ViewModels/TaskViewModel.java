package com.example.myapplication.MainLogic.UI.ViewModels;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.MainLogic.Data.Model.TaskItemBean;
import com.example.myapplication.MainLogic.Data.Repository.TaskRepository;
import com.example.myapplication.MainLogic.UI.TaskAdapter;

import java.util.List;

import Database.BatchWorker.NotificationWorker;

public class TaskViewModel extends ViewModel {
    private final TaskRepository repository;

    public TaskViewModel(@NonNull Context context) {
        repository = new TaskRepository(context);
    }

    public void loadTasks(List<TaskItemBean> tasks, int dayValue, TaskAdapter adapter) {
        repository.loadTasksForDay(tasks, dayValue, adapter);
        adapter.sortTasks();
    }

    public long insertTask(TaskItemBean task, int dayValue) {
        return repository.insertTask(task, dayValue);
    }

    public void deleteTask(TaskItemBean task, int dayValue) {
        repository.deleteTask(task, dayValue);
    }

    public void updateTask(TaskItemBean task, int dayValue) {
        repository.updateTask(task, dayValue);
    }

    public void closeDBManager() {
        repository.closeDbManager();
    }

    public void runBatchOnActivity() {
        NotificationWorker.runBatchNotif(repository.getDbManager());
    }
}
