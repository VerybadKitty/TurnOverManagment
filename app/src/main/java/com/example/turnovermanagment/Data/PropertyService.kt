package com.example.turnovermanagment.Data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PropertyService(private val databaseManager: DatabaseManager) {

    fun addProperty(property: Property) = flow {
        emit(databaseManager.addProperty(property))
    }.flowOn(Dispatchers.IO)

    fun updateProperty(propertyId: String, propertyUpdates: Map<String, Any>) = flow {
        emit(databaseManager.updateProperty(propertyId, propertyUpdates))
    }.flowOn(Dispatchers.IO)

    fun getPropertyDetails(propertyId: String) = flow {
        emit(databaseManager.getProperty(propertyId))
    }.flowOn(Dispatchers.IO)

    fun retrieveUnits(propertyId: String) = flow {
        emit(databaseManager.getUnitsForProperty(propertyId))
    }.flowOn(Dispatchers.IO)

    fun addUnit(propertyId: String, unit: PropertyUnit) = flow {
        emit(databaseManager.addUnit(propertyId, unit))
    }.flowOn(Dispatchers.IO)

    fun getProperties() = flow {
        emit(databaseManager.getProperties())
    }.flowOn(Dispatchers.IO)

    fun deleteProperty(propertyId: String) = flow {
        emit(databaseManager.deleteProperty(propertyId))
    }.flowOn(Dispatchers.IO)
}



