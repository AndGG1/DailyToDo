package com.example.myapplication.MainLogic.UI;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.myapplication.MainLogic.Data.Model.TaskItemBean;
import com.example.myapplication.MainLogic.Data.Repository.TaskRepository;

import java.util.List;

public class TaskViewModel  {
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
}