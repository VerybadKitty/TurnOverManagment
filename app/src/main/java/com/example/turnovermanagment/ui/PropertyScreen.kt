package com.example.turnovermanagment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.turnovermanagment.Data.Property
import androidx.compose.ui.tooling.preview.Preview
import com.example.turnovermanagment.utils.Resource
import com.example.turnovermanagment.Data.PropertyService

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
        onRefresh = { propertyViewModel.loadAllProperties() }
    )
}

@Composable
fun PropertyScreenContent(
    propertiesState: Resource<List<Property>>,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onRefresh) {
                Text("Refresh")
            }
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        errorMessage?.let {
            Text("Error: $it", style = MaterialTheme.typography.body2)
        }

        when (propertiesState) {
            is Resource.Success -> {
                PropertyList(properties = propertiesState.data ?: emptyList())
            }
            is Resource.Error -> {
                Text("Failed to load properties: ${propertiesState.message}", style = MaterialTheme.typography.body2)
            }
            is Resource.Loading -> {
                CircularProgressIndicator()
            }
            else -> {
                Text("No properties available", style = MaterialTheme.typography.body2)
            }
        }
    }
}

@Composable
fun PropertyList(properties: List<Property>) {
    LazyColumn {
        items(properties) { property ->
            PropertyListItem(property)
        }
    }
}

@Composable
fun PropertyListItem(property: Property) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
        elevation = 2.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Address: ${property.address}", style = MaterialTheme.typography.h6)
            Text("Units: ${property.numberOfUnits}", style = MaterialTheme.typography.body1)
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
        onRefresh = {}
    )
}


