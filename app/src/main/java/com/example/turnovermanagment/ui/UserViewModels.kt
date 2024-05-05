package com.example.turnovermanagment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import com.example.turnovermanagment.Data.UserService
import com.example.turnovermanagment.Data.User
import com.example.turnovermanagment.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel(private val userService: UserService) : ViewModel() {
    private val _users = MutableStateFlow<Resource<List<User>>>(Resource.Loading())
    val users: StateFlow<Resource<List<User>>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadUsers()
    }

    // Changed from private to public
    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            userService.listUsers()
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    _users.value = Resource.Error("Failed to load users: ${e.message}")
                }
                .collect { result ->
                    _isLoading.value = false
                    _users.value = Resource.Success(result.getOrElse { emptyList() })
                }
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            userService.addUser(user)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    _users.value = Resource.Error("Failed to add user: ${e.message}")
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadUsers()  // Refresh the user list
                    }
                }
        }
    }

    fun updateUser(userId: String, updates: Map<String, Any>) {
        viewModelScope.launch {
            _isLoading.value = true
            userService.updateUser(userId, updates)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    _users.value = Resource.Error("Failed to update user: ${e.message}")
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadUsers()  // Refresh the user list
                    }
                }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            userService.deleteUser(userId)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    _users.value = Resource.Error("Failed to delete user: ${e.message}")
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadUsers()  // Refresh the user list after deletion
                    }
                }
        }
    }
}

