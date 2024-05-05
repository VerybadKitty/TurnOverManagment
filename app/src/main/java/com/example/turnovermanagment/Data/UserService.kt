package com.example.turnovermanagment.Data


import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn


class UserService(private val databaseManager: DatabaseManager) {

    fun addUser(user: User) = flow {
        emit(databaseManager.addUser(user))
    }.flowOn(Dispatchers.IO)

    fun updateUser(userId: String, updates: Map<String, Any>) = flow {
        emit(databaseManager.updateUser(userId, updates))
    }.flowOn(Dispatchers.IO)

    fun deleteUser(userId: String) = flow {
        emit(databaseManager.deleteUser(userId))
    }.flowOn(Dispatchers.IO)

    fun listUsers() = flow {
        emit(databaseManager.getUsers())
    }.flowOn(Dispatchers.IO)
}


