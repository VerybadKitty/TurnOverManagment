package com.example.turnovermanagment.Data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch


class UserService(private val databaseManager: DatabaseManager) {

    suspend fun addUser(user: User): Flow<Result<Unit>> = flow {
        emit(Result.success(databaseManager.addUser(user).await()))
    }.catch { e ->
        emit(Result.failure(e))
    }

    suspend fun updateUser(userId: String, updates: Map<String, Any>): Flow<Result<Unit>> = flow {
        emit(Result.success(databaseManager.updateUser(userId, updates).await()))
    }.catch { e ->
        emit(Result.failure(e))
    }

    suspend fun deleteUser(userId: String): Flow<Result<Unit>> = flow {
        emit(Result.success(databaseManager.deleteUser(userId).await()))
    }.catch { e ->
        emit(Result.failure(e))
    }

    fun listUsers(): Flow<Result<List<User>>> = flow {
        emit(Result.success(databaseManager.getUsers().await()))
    }.catch { e ->
        emit(Result.failure(e))
    }
}

