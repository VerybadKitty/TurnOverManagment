package com.example.turnovermanagment.ui;


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turnovermanagment.Data.TaskService
import com.example.turnovermanagment.Data.Task
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import com.example.turnovermanagment.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class TasksViewModel(private val taskService: TaskService) : ViewModel() {
    private val _tasks = MutableStateFlow<Resource<List<Task>>>(Resource.Loading())
    val tasks: StateFlow<Resource<List<Task>>> = _tasks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()



    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            taskService.retrieveTasks()
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    _tasks.value = Resource.Error("Failed to load tasks: ${e.message}")
                }
                .collect { result ->
                    _isLoading.value = false
                    _tasks.value = Resource.Success(result.getOrElse { emptyList() })
                }
        }
    }

    fun createTask(task: Task) {
        viewModelScope.launch {
            _isLoading.value = true
            taskService.createTask(task)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    _tasks.value = Resource.Error("Failed to create task: ${e.message}")
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadTasks()  // Refresh the task list after adding a new task
                    }
                }
        }
    }

    fun updateTask(taskId: String, taskUpdates: Map<String, Any>) {
        viewModelScope.launch {
            _isLoading.value = true
            taskService.updateTask(taskId, taskUpdates)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadTasks()  // Refresh the task list after an update
                    }
                }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            taskService.deleteTask(taskId)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadTasks()  // Refresh the task list after deletion
                    }
                }
        }
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            taskService.completeTask(taskId)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadTasks()  // Refresh the task list to show the task as completed
                    }
                }
        }
    }

    fun assignTask(taskId: String, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            taskService.assignTask(taskId, userId)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadTasks()  // Refresh the task list to reflect the new assignment
                    }
                }
        }
    }
}



