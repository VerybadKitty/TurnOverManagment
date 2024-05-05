package com.example.turnovermanagment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.turnovermanagment.Data.PropertyService
import com.example.turnovermanagment.Data.QuestionQueueService
import com.example.turnovermanagment.Data.ReviewService
import com.example.turnovermanagment.Data.TaskService
import com.example.turnovermanagment.Data.UserService
import java.lang.IllegalArgumentException

class ViewModelFactory(private val service: Any) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            PropertyViewModel::class.java -> PropertyViewModel(service as PropertyService)
            TasksViewModel::class.java -> TasksViewModel(service as TaskService)
            QuestionsViewModel::class.java -> QuestionsViewModel(service as QuestionQueueService)
            ReviewViewModel::class.java -> ReviewViewModel(service as ReviewService)
            UserViewModel::class.java -> UserViewModel(service as UserService)
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        } as T
    }
}
