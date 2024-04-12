package com.example.turnovermanagment.Data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import java.util.Date
import java.util.UUID

class ReviewService(private val databaseManager: DatabaseManager) {

    suspend fun submitReview(review: UnitReview): Flow<Result<Unit>> = flow {
        emit(Result.success(databaseManager.addReview(review).await()))
    }.catch { e ->
        emit(Result.failure(e))
    }

    fun generateTasksFromReview(review: UnitReview): Flow<Result<Unit>> = flow {
        review.observations.forEach { observation ->
            if (observation.severity == Severity.HIGH || observation.severity == Severity.CRITICAL) {
                val task = Task(
                    id = generateTaskId(),
                    description = "Repair required for ${observation.area} due to ${observation.issueType}: ${observation.description}",
                    assignedTo = "",  // Logic for assigning the task should be updated based on application requirements
                    priority = if (observation.severity == Severity.CRITICAL) 1 else 2,
                    status = "Pending",
                    dueDate = calculateDueDate(),
                    relatedUnitId = review.unitId
                )
                emit(Result.success(databaseManager.addTask(task).await()))
            }
        }
    }.catch { e ->
        emit(Result.failure(e))
    }

    private fun generateTaskId(): String {
        return UUID.randomUUID().toString()
    }

    private fun calculateDueDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 3)  // Assuming a 3-day period for task due date
        return calendar.time
    }
}

