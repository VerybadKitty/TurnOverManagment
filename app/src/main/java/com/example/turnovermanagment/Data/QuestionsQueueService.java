package com.example.turnovermanagment.Data;

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuestionsQueueService(private val databaseManager:DatabaseManager) {

    fun submitQuestion(question: Question): Flow<Result<Unit>> = flow {
        emit(Result.success(databaseManager.addQuestion(question).await()))
    }.catch { e ->
            emit(Result.failure(e))
    }

    fun retrieveQuestions(): Flow<Result<List<Question>>> = flow {
        emit(Result.success(databaseManager.getQuestions().await()))
    }.catch { e ->
            emit(Result.failure(e))
    }

    fun respondToQuestion(questionId: String, response: String): Flow<Result<Unit>> = flow {
        emit(Result.success(databaseManager.updateQuestion(questionId, response).await()))
    }.catch { e ->
            emit(Result.failure(e))
    }

    fun prioritizeQuestion(questionId: String, priority: Int): Flow<Result<Unit>> = flow {
        emit(Result.success(databaseManager.updateQuestionPriority(questionId, priority).await()))
    }.catch { e ->
            emit(Result.failure(e))
    }

    fun archiveQuestion(questionId: String): Flow<Result<Unit>> = flow {
        emit(Result.success(databaseManager.archiveQuestion(questionId).await()))
    }.catch { e ->
            emit(Result.failure(e))
    }
}



