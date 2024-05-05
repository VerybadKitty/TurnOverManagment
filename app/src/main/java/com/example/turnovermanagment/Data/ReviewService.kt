package com.example.turnovermanagment.Data


import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class ReviewService(private val databaseManager: DatabaseManager) {

    fun submitReview(review: UnitReview) = flow {
        emit(Result.success(databaseManager.addReview(review).getOrThrow()))
    }.flowOn(Dispatchers.IO)

    fun retrieveAllReviews() = flow {
        emit(Result.success(databaseManager.getReviews().getOrThrow()))
    }.flowOn(Dispatchers.IO)

    fun updateReview(reviewId: String, reviewUpdates: Map<String, Any>) = flow {
        emit(Result.success(databaseManager.updateReview(reviewId, reviewUpdates).getOrThrow()))
    }.flowOn(Dispatchers.IO)

    fun deleteReview(reviewId: String) = flow {
        emit(Result.success(databaseManager.deleteReview(reviewId).getOrThrow()))
    }.flowOn(Dispatchers.IO)
}



