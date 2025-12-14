package com.example.myapplication.MainLogic.UI.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.MainLogic.Data.Model.TaskItemBean
import com.example.myapplication.MainLogic.Data.Repository.TaskRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    val copyOfRepo = repository

    private val _tasks = MutableLiveData<List<TaskItemBean>>(emptyList())
    val tasks: LiveData<List<TaskItemBean>> get() = _tasks

    private val _state = MutableStateFlow<UIState2>(UIState2.STANDING)
    val state: StateFlow<UIState2> = _state


    fun loadTasks(dayValue: Int) {
        _state.value = UIState2.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            _tasks.postValue(repository.loadTasksForDay(dayValue))
        }
        _state.value = UIState2.STANDING
    }

    fun insertTask(task: TaskItemBean, dayValue: Int) {
        _state.value = UIState2.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(task, dayValue)
            _tasks.postValue(repository.loadTasksForDay(dayValue))
        }
        _state.value = UIState2.STANDING
    }

    fun deleteTask(task: TaskItemBean, dayValue: Int) {
        _state.value = UIState2.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(task, dayValue)
            _tasks.postValue(repository.loadTasksForDay(dayValue))
        }
        _state.value = UIState2.STANDING
    }

    fun updateTask(task: TaskItemBean, dayValue: Int) {
        _state.value = UIState2.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task, dayValue)
            _tasks.postValue(repository.loadTasksForDay(dayValue))
        }
        _state.value = UIState2.STANDING
    }

    fun closeDBManager() {
        repository.closeDbManager()
    }

    fun check() : Boolean {
        return state.value == UIState2.STANDING
    }
}

sealed class UIState2 {
    object LOADING: UIState2()
    object STANDING: UIState2()
}
