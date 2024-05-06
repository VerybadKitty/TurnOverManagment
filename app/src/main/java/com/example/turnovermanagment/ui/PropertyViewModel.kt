package com.example.turnovermanagment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import com.example.turnovermanagment.Data.PropertyService
import com.example.turnovermanagment.Data.Property
import com.example.turnovermanagment.Data.PropertyUnit
import com.example.turnovermanagment.utils.Resource
import kotlinx.coroutines.flow.fold

class PropertyViewModel(private val propertyService: PropertyService) : ViewModel() {
    private val _properties = MutableStateFlow<Resource<List<Property>>>(Resource.Loading())
    val properties: StateFlow<Resource<List<Property>>> = _properties.asStateFlow()

    private val _units = MutableStateFlow<Resource<List<PropertyUnit>>>(Resource.Loading())
    val units: StateFlow<Resource<List<PropertyUnit>>> = _units.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadAllProperties()
    }

    fun loadPropertyDetails(propertyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            propertyService.getPropertyDetails(propertyId).catch { e ->
                _errorMessage.value = e.message ?: "Failed to load property details"
                _properties.value = Resource.Error(_errorMessage.value!!)
                _isLoading.value = false
            }.collect { result ->
                handleSinglePropertyResult(result, _properties)
            }
        }
    }

    private fun handleSinglePropertyResult(result: Result<Property?>, stateFlow: MutableStateFlow<Resource<List<Property>>>) {
        result.fold(
            onSuccess = { property ->
                property?.let { stateFlow.value = Resource.Success(listOf(it)) } ?: run {
                    stateFlow.value = Resource.Success(emptyList())
                }
                _isLoading.value = false
            },
            onFailure = { exception ->
                _errorMessage.value = exception.localizedMessage ?: "Unknown error"
                stateFlow.value = Resource.Error(_errorMessage.value!!)
                _isLoading.value = false
            }
        )
    }

    fun loadUnits(propertyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            propertyService.retrieveUnits(propertyId)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    _units.value = Resource.Error("Failed to load units: ${e.message}")
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        _units.value = Resource.Success(result.getOrNull() ?: emptyList())
                    } else {
                        _errorMessage.value = result.exceptionOrNull()?.message ?: "Unknown error"
                        _units.value = Resource.Error(_errorMessage.value!!)
                    }
                }
        }
    }

    fun addUnit(propertyId: String, unit: PropertyUnit) {
        viewModelScope.launch {
            _isLoading.value = true
            propertyService.addUnit(propertyId, unit)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadUnits(propertyId)  // Reload units to reflect the added unit
                    } else {
                        _errorMessage.value = "Failed to add unit: ${result.exceptionOrNull()?.message}"
                    }
                }
        }
    }

    fun loadAllProperties() {
        viewModelScope.launch {
            _isLoading.value = true
            propertyService.getProperties()
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    _properties.value = Resource.Error("Failed to load properties: ${e.message}")
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        _properties.value = Resource.Success(result.getOrNull() ?: emptyList())
                    } else {
                        _errorMessage.value = result.exceptionOrNull()?.message ?: "Unknown error"
                        _properties.value = Resource.Error(_errorMessage.value!!)
                    }
                }
        }
    }

    fun addProperty(property: Property) {
        viewModelScope.launch {
            _isLoading.value = true
            propertyService.addProperty(property)
                .catch { e ->
                    // Handle any exceptions that occur during the network call or processing
                    _errorMessage.value = e.message ?: "Failed to add property"
                    _isLoading.value = false
                }
                .collect { result ->
                    result.fold(
                        onSuccess = {
                            loadAllProperties()
                            _isLoading.value = false
                        },
                        onFailure = { exception ->
                            // On failure, post an error message
                            _errorMessage.value = exception.message ?: "Failed to add property"
                            _isLoading.value = false
                        }
                    )
                }
        }
    }


    fun deleteProperty(propertyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            propertyService.deleteProperty(propertyId)
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message
                }
                .collect { result ->
                    _isLoading.value = false
                    if (result.isSuccess) {
                        loadAllProperties()  // Reload properties to reflect the deletion
                    } else {
                        _errorMessage.value = "Failed to delete property: ${result.exceptionOrNull()?.message}"
                    }
                }
        }
    }
}
