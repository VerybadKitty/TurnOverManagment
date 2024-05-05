package com.example.turnovermanagment.Data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class TaskService(private val databaseManager: DatabaseManager) {

    fun createTask(task: Task): Flow<Result<Unit>> = flow {
        emit(databaseManager.addTask(task))
    }.flowOn(Dispatchers.IO)

    fun updateTask(taskId: String, updates: Map<String, Any>): Flow<Result<Unit>> = flow {
        emit(databaseManager.updateTask(taskId, updates))
    }.flowOn(Dispatchers.IO)

    fun retrieveTasks(): Flow<Result<List<Task>>> = flow {
        emit(databaseManager.getTasks())
    }.flowOn(Dispatchers.IO)

    fun deleteTask(taskId: String): Flow<Result<Unit>> = flow {
        emit(databaseManager.deleteTask(taskId))
    }.flowOn(Dispatchers.IO)

    fun completeTask(taskId: String): Flow<Result<Unit>> = flow {
        emit(databaseManager.completeTask(taskId))
    }.flowOn(Dispatchers.IO)

    fun assignTask(taskId: String, userId: String): Flow<Result<Unit>> = flow {
        emit(databaseManager.assignTask(taskId, userId))
    }.flowOn(Dispatchers.IO)
}




