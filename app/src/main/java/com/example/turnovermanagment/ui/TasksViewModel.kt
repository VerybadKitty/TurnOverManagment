package com.example.turnovermanagment.ui;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turnovermanagment.Data.TaskService
import kotlinx.coroutines.launch

class TasksViewModel(private val taskService: TaskService) : ViewModel() {

    val tasks = MutableLiveData<Result<List<Task>>>()

    fun loadTasks() {
        viewModelScope.launch {
            taskService.retrieveTasks().collect {
                tasks.value = it
            }
        }
    }
}



