package com.example.turnovermanagment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.turnovermanagment.Data.Property
import androidx.compose.ui.tooling.preview.Preview
import com.example.turnovermanagment.utils.Resource
import com.example.turnovermanagment.Data.PropertyService
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import com.example.turnovermanagment.Data.PropertyUnit
import java.util.Date


@Composable
fun PropertyScreen(propertyService: PropertyService) {
    val propertyViewModel: PropertyViewModel = viewModel(factory = ViewModelFactory(propertyService))
    val propertiesState = propertyViewModel.properties.collectAsState()
    val isLoading = propertyViewModel.isLoading.collectAsState()
    val errorMessage = propertyViewModel.errorMessage.collectAsState()

    PropertyScreenContent(
        propertiesState = propertiesState.value,
        isLoading = isLoading.value,
        errorMessage = errorMessage.value,
        onRefresh = { propertyViewModel.loadAllProperties() },
        propertyViewModel = propertyViewModel
    )
}

@Composable
fun PropertyScreenContent(
    propertiesState: Resource<List<Property>>,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit,
    propertyViewModel: PropertyViewModel
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onRefresh) {
                Text("Refresh")
            }
            Button(onClick = { showDialog = true }) {
                Text("Add Property")
            }
        }

        if (showDialog) {
            AddPropertyDialog(propertyViewModel = propertyViewModel, onDismiss = { showDialog = false })
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        errorMessage?.let {
            if (it.isNotEmpty()) {
                Text("Error: $it", style = MaterialTheme.typography.body2, color = MaterialTheme.colors.error)
            }
        }

        when (propertiesState) {
            is Resource.Success -> {
                if (propertiesState.data.isNullOrEmpty()) {
                    Text("No properties available", style = MaterialTheme.typography.body1)
                } else {
                    PropertyList(properties = propertiesState.data, propertyViewModel = propertyViewModel)
                }
            }
            is Resource.Error -> {
                Text("Failed to load properties: ${propertiesState.message}", style = MaterialTheme.typography.body2, color = MaterialTheme.colors.error)
            }
            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else -> {}
        }
    }
}

@Composable
fun AddPropertyDialog(propertyViewModel: PropertyViewModel, onDismiss: () -> Unit) {
    val (address, setAddress) = remember { mutableStateOf("") }
    val (units, setUnits) = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Property") },
        text = {
            Column {
                TextField(
                    value = address,
                    onValueChange = setAddress,
                    label = { Text("Address") }
                )
                TextField(
                    value = units,
                    onValueChange = setUnits,
                    label = { Text("Number of Units") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (address.isNotBlank() && units.toIntOrNull() != null) {
                        val newProperty = Property(
                            address = address,
                            numberOfUnits = units.toIntOrNull() ?: 0,
                            createdAt = Date(),
                            updatedAt = Date()
                        )
                        propertyViewModel.addProperty(newProperty)
                        onDismiss()
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddUnitDialog(propertyId: String, propertyViewModel: PropertyViewModel, onDismiss: () -> Unit) {
    val (unitNumber, setUnitNumber) = remember { mutableStateOf("") }
    val (condition, setCondition) = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Unit") },
        text = {
            Column {
                TextField(
                    value = unitNumber,
                    onValueChange = setUnitNumber,
                    label = { Text("Unit Number") }
                )
                TextField(
                    value = condition,
                    onValueChange = setCondition,
                    label = { Text("Condition") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (unitNumber.isNotBlank()) {
                        val newUnit = PropertyUnit(
                            propertyId = propertyId,
                            unitNumber = unitNumber,
                            condition = condition,
                            hasReview = false
                        )
                        propertyViewModel.addUnit(propertyId, newUnit)
                        onDismiss()
                    }
                }
            ) {
                Text("Add Unit")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun PropertyListItem(property: Property, propertyViewModel: PropertyViewModel) {
    var showUnitDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
        elevation = 2.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Address: ${property.address}", style = MaterialTheme.typography.h6)
            Text("Units: ${property.numberOfUnits}", style = MaterialTheme.typography.body1)
            Button(onClick = { showUnitDialog = true }) {
                Text("Add/Manage Units")
            }
            if (showUnitDialog) {
                AddUnitDialog(propertyId = property.id, propertyViewModel = propertyViewModel, onDismiss = { showUnitDialog = false })
            }
        }
    }
}

@Composable
fun PropertyList(properties: List<Property>, propertyViewModel: PropertyViewModel) {
    LazyColumn {
        items(properties) { property ->
            PropertyListItem(property, propertyViewModel)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PropertyScreenContent(
        propertiesState = Resource.Success(listOf(
            Property("1", "123 Main St", 10, null, null),
            Property("2", "456 Elm St", 5, null, null)
        )),
        isLoading = false,
        errorMessage = null,
        onRefresh = {},
        propertyViewModel = viewModel() // This is just for the preview; not executable in this context
    )
}

