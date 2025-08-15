package com.example.myapplication.MainLogic.UI;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.MainLogic.Data.Model.TaskItemBean;
import com.example.myapplication.MainLogic.Data.Repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private final TaskRepository repository;
    private final MutableLiveData<List<TaskItemBean>> tasksLiveData = new MutableLiveData<>();
    private final int DEFAULT_DAY_VALUE = 2;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        loadTasks(DEFAULT_DAY_VALUE);
    }

    private void loadTasks(int dayValue) {
        List<TaskItemBean> tasks = repository.loadTasksForDay(dayValue);
        tasksLiveData.setValue(tasks);
    }

    public LiveData<List<TaskItemBean>> getTasks(int dayValue) {
        loadTasks(dayValue);
        return tasksLiveData;
    }

    public void insertTask(TaskItemBean task, int dayValue) {
        repository.insertTask(task, dayValue);
    }

    public void deleteTask(TaskItemBean task, int dayValue) {
        repository.deleteTask(task, dayValue);
    }

    public void updateTask(TaskItemBean task, int dayValue) {
        repository.updateTask(task, dayValue);
    }
}
