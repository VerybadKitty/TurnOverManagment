package com.example.turnovermanagment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.turnovermanagment.Data.Observation
import com.example.turnovermanagment.Data.ReviewService
import com.example.turnovermanagment.Data.UnitReview
import com.example.turnovermanagment.utils.Resource

@Composable
fun ReviewsScreen(reviewService: ReviewService) {
    val reviewViewModel: ReviewViewModel = viewModel(factory = ViewModelFactory(reviewService))
    val reviewsState by reviewViewModel.reviews.collectAsState()
    val isLoading by reviewViewModel.isLoading.collectAsState()
    val errorMessage by reviewViewModel.errorMessage.collectAsState()

    ReviewsContent(
        reviewsState = reviewsState,
        isLoading = isLoading,
        errorMessage = errorMessage,
        onAddReview = { review -> reviewViewModel.submitReview(review) }
    )
}

@Composable
fun ReviewsContent(
    reviewsState: Resource<List<UnitReview>>,
    isLoading: Boolean,
    errorMessage: String?,
    onAddReview: (UnitReview) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AddReviewDialog(
            onAddReview = {
                onAddReview(it)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { showDialog = true }) {
                Text("Add Review")
            }
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        errorMessage?.let {
            if (it.isNotEmpty()) {
                Text("Error: $it", style = MaterialTheme.typography.body2)
            }
        }

        when (reviewsState) {
            is Resource.Success -> {
                if (reviewsState.data.isNullOrEmpty()) {
                    Text("No reviews available", style = MaterialTheme.typography.body2)
                } else {
                    ReviewList(reviewsState.data)
                }
            }
            is Resource.Error -> {
                Text("Error loading reviews: ${reviewsState.message}", style = MaterialTheme.typography.body2)
            }
            is Resource.Loading -> {
                CircularProgressIndicator()
            }
            else -> {
                Text("Unexpected state", style = MaterialTheme.typography.body2)
            }
        }
    }
}

@Composable
fun ReviewList(reviews: List<UnitReview>) {
    LazyColumn {
        items(reviews) { review ->
            ReviewListItem(review)
        }
    }
}

@Composable
fun ReviewListItem(review: UnitReview) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
        elevation = 2.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Reviewed by: ${review.reviewerId}", style = MaterialTheme.typography.h6)
            Text("Date: ${review.dateReviewed.toString()}", style = MaterialTheme.typography.body1)
            Text("Overall Condition: ${review.overallCondition}", style = MaterialTheme.typography.body1)
            Text("Observations: ${review.observations.joinToString(", ") { it.description }}", style = MaterialTheme.typography.body2)
        }
    }
}

@Composable
fun AddReviewDialog(onAddReview: (UnitReview) -> Unit, onDismiss: () -> Unit) {
    var reviewerId by remember { mutableStateOf("") }
    var overallCondition by remember { mutableStateOf("") }
    var unitId by remember { mutableStateOf("") }
    var reviewText by remember { mutableStateOf("") }
    var observationsText by remember { mutableStateOf("") }
    var photosUrlsText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Review") },
        text = {
            Column {
                TextField(
                    value = reviewerId,
                    onValueChange = { reviewerId = it },
                    label = { Text("Reviewer ID") }
                )
                TextField(
                    value = unitId,
                    onValueChange = { unitId = it },
                    label = { Text("Unit ID") }
                )
                TextField(
                    value = overallCondition,
                    onValueChange = { overallCondition = it },
                    label = { Text("Overall Condition") }
                )
                TextField(
                    value = observationsText,
                    onValueChange = { observationsText = it },
                    label = { Text("Observations (comma-separated)") }
                )
                TextField(
                    value = photosUrlsText,
                    onValueChange = { photosUrlsText = it },
                    label = { Text("Photo URLs (comma-separated)") }
                )
                TextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Review Text") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Parse the observations and photo URLs from comma-separated strings to lists
                    val observationsList = observationsText.split(",").map { it.trim() }
                    val photosUrlsList = photosUrlsText.split(",").map { it.trim() }

                    onAddReview(UnitReview(
                        id = "new", // This should ideally be generated server-side or omitted for Firestore to auto-generate
                        reviewerId = reviewerId,
                        unitId = unitId,
                        dateReviewed = System.currentTimeMillis(),
                        overallCondition = overallCondition,
                        observations = observationsList.map { Observation(description = it) }, // Simplified mapping
                        photosUrls = photosUrlsList,
                        reviewText = reviewText
                    ))
                    onDismiss()
                }
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
