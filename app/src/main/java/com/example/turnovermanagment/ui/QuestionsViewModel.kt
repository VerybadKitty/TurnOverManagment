package com.example.turnovermanagment.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.turnovermanagment.Data.QuestionsQueueService

class QuestionsViewModel(private val questionsQueueService: QuestionsQueueService) : ViewModel() {

    val questions = MutableLiveData<Result<List<Question>>>()

    fun loadQuestions() {
        viewModelScope.launch {
            questionsQueueService.retrieveQuestions().collect {
                questions.value = it
            }
        }
    }
}
