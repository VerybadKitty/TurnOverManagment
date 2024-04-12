package com.example.turnovermanagment.Data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch

class TaskService(private val databaseManager: DatabaseManager) {

    suspend fun createTask(task: Task): Flow<Result<Unit>> = flow {
        emit(Result.success(databaseManager.addTask(task).await()))
    }.catch { e ->
        emit(Result.failure(e))
    }

    suspend fun assignTask(taskId: String, userId: String): Flow<Result<Unit>> = flow {
        emit(Result.success(databaseManager.updateTask(taskId, mapOf("assignedTo" to userId)).await()))
    }.catch { e ->
        emit(Result.failure(e))
    }

    suspend fun updateTask(taskId: String, taskUpdates: Map<String, Any>): Flow<Result<Unit>> = flow {
        emit(Result.success(databaseManager.updateTask(taskId, taskUpdates).await()))
    }.catch { e ->
        emit(Result.failure(e))
    }

    suspend fun completeTask(taskId: String): Flow<Result<Unit>> = flow {
        emit(Result.success(databaseManager.updateTask(taskId, mapOf("status" to "Completed")).await()))
    }.catch { e ->
        emit(Result.failure(e))
    }
}
