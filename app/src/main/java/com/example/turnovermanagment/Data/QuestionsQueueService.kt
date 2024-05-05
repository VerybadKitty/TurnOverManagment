package com.example.turnovermanagment.Data


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn




class QuestionQueueService(private val databaseManager: DatabaseManager) {

    fun addQuestion(question: Question) = flow {
        emit(databaseManager.addQuestion(question))
    }.flowOn(Dispatchers.IO)

    fun retrieveQuestions() = flow {
        emit(databaseManager.getQuestions())
    }.flowOn(Dispatchers.IO)

    fun updateQuestion(questionId: String, updates: Map<String, Any>) = flow {
        emit(databaseManager.updateQuestion(questionId, updates))
    }.flowOn(Dispatchers.IO)

    fun prioritizeQuestion(questionId: String, priority: Map<String, Int>) = flow {
        val updates = mapOf("priority" to priority)
        emit(databaseManager.updateQuestion(questionId, updates))
    }.flowOn(Dispatchers.IO)

    fun archiveQuestion(questionId: String) = flow {
        emit(databaseManager.archiveQuestion(questionId))
    }.flowOn(Dispatchers.IO)
}












