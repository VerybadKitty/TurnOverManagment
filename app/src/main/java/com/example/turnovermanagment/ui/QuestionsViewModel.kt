package com.example.turnovermanagment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turnovermanagment.Data.Question
import com.example.turnovermanagment.Data.QuestionQueueService
import com.example.turnovermanagment.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class QuestionsViewModel(private val questionsQueueService: QuestionQueueService) : ViewModel() {
    private val _questions = MutableStateFlow<Resource<List<Question>>>(Resource.Loading())
    val questions: StateFlow<Resource<List<Question>>> = _questions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadQuestions()
    }

    fun loadQuestions() {
        viewModelScope.launch {
            _isLoading.value = true
            questionsQueueService.retrieveQuestions()
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    _questions.value = Resource.Error("Failed to load questions: ${e.message}")
                }
                .collect { result ->
                    _isLoading.value = false
                    _questions.value = Resource.Success(result.getOrNull() ?: listOf())
                }
        }
    }

    fun addQuestion(question: Question) {
        viewModelScope.launch {
            _isLoading.value = true
            questionsQueueService.addQuestion(question)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadQuestions()  // Refresh the list after adding a new question
                    } else {
                        _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to add question"
                    }
                }
        }
    }

    fun updateQuestion(questionId: String, response: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val updates = mapOf("response" to response)
            questionsQueueService.updateQuestion(questionId, updates)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadQuestions()  // Refresh the list after updating a question
                    } else {
                        _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to update question"
                    }
                }
        }
    }

    fun prioritizeQuestion(questionId: String, priority: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val updates = mapOf("priority" to priority)
            questionsQueueService.prioritizeQuestion(questionId, updates)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadQuestions()  // Refresh the list after changing priority
                    } else {
                        _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to prioritize question"
                    }
                }
        }
    }

    fun archiveQuestion(questionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            questionsQueueService.archiveQuestion(questionId)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadQuestions()  // Refresh the list after archiving a question
                    } else {
                        _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to archive question"
                    }
                }
        }
    }
}




