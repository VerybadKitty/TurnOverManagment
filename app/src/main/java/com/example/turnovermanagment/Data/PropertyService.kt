package com.example.turnovermanagment.Data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch

class PropertyService(private val databaseManager: DatabaseManager) {

    suspend fun addProperty(property: Property): Flow<Result<Unit>> = flow {
        emit(Result.success(databaseManager.addProperty(property).await()))
    }.catch { e ->
        emit(Result.failure(e))
    }

    suspend fun updateProperty(propertyId: String, propertyUpdates: Map<String, Any>): Flow<Result<Unit>> = flow {
        emit(Result.success(databaseManager.updateProperty(propertyId, propertyUpdates).await()))
    }.catch { e ->
        emit(Result.failure(e))
    }

    suspend fun getPropertyDetails(propertyId: String): Flow<Result<Property>> = flow {
        val property = databaseManager.getProperty(propertyId).await()
        if (property != null) {
            emit(Result.success(property))
        } else {
            throw Exception("Property not found")
        }
    }.catch { e ->
        emit(Result.failure(e))
    }

    suspend fun addUnit(propertyId: String, unit: Unit): Flow<Result<Unit>> = flow {
        emit(Result.success(databaseManager.addUnit(propertyId, unit).await()))
    }.catch { e ->
        emit(Result.failure(e))
    }

    suspend fun updateUnit(unitId: String, unitUpdates: Map<String, Any>): Flow<Result<Unit>> = flow {
        emit(Result.success(databaseManager.updateUnit(unitId, unitUpdates).await()))
    }.catch { e ->
        emit(Result.failure(e))
    }
}