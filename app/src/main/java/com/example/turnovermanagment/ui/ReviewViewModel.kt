package com.example.turnovermanagment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turnovermanagment.Data.ReviewService
import com.example.turnovermanagment.Data.UnitReview
import com.example.turnovermanagment.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ReviewViewModel(private val reviewService: ReviewService) : ViewModel() {
    private val _reviews = MutableStateFlow<Resource<List<UnitReview>>>(Resource.Loading())
    val reviews: StateFlow<Resource<List<UnitReview>>> = _reviews.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadReviews()
    }

    fun loadReviews() {
        viewModelScope.launch {
            _isLoading.value = true
            reviewService.retrieveAllReviews()
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    _reviews.value = Resource.Error("Failed to load reviews: ${e.message}")
                }
                .collect { result ->
                    _isLoading.value = false
                    _reviews.value = Resource.Success(result.getOrDefault(emptyList()))
                }
        }
    }

    fun submitReview(review: UnitReview) {
        viewModelScope.launch {
            _isLoading.value = true
            reviewService.submitReview(review)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    _reviews.value = Resource.Error("Failed to submit review: ${e.message}")
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadReviews() // Refresh the list after submitting a new review
                    }
                }
        }
    }

    fun updateReview(reviewId: String, reviewUpdates: Map<String, Any>) {
        viewModelScope.launch {
            _isLoading.value = true
            reviewService.updateReview(reviewId, reviewUpdates)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    _reviews.value = Resource.Error("Failed to update review: ${e.message}")
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadReviews() // Refresh the list to show updated review
                    }
                }
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            reviewService.deleteReview(reviewId)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    _reviews.value = Resource.Error("Failed to delete review: ${e.message}")
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadReviews() // Refresh the list to remove the deleted review
                    }
                }
        }
    }
}
