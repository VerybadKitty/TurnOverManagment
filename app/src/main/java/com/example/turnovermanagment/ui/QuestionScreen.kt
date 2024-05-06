package com.example.turnovermanagment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.turnovermanagment.Data.Question
import com.example.turnovermanagment.Data.QuestionQueueService
import com.example.turnovermanagment.ui.QuestionsViewModel
import com.example.turnovermanagment.utils.Resource

@Composable
fun QuestionsScreen(questionQueueService: QuestionQueueService) {
    val questionsViewModel: QuestionsViewModel = viewModel(factory = ViewModelFactory(questionQueueService))
    val questionsState by questionsViewModel.questions.collectAsState()
    val isLoading by questionsViewModel.isLoading.collectAsState()
    val errorMessage by questionsViewModel.errorMessage.collectAsState()

    QuestionsContent(
        questionsState = questionsState,
        isLoading = isLoading,
        errorMessage = errorMessage,
        onRefresh = { questionsViewModel.loadQuestions() },
        onAddQuestion = { questionsViewModel.addQuestion(it) }
    )
}


@Composable
fun QuestionsContent(
    questionsState: Resource<List<Question>>,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit,
    onAddQuestion: (Question) -> Unit
) {
    var showAddQuestionDialog by remember { mutableStateOf(false) }

    if (showAddQuestionDialog) {
        AddQuestionDialog(onAddQuestion = {
            onAddQuestion(it)
            showAddQuestionDialog = false
        }, onDismiss = { showAddQuestionDialog = false })
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onRefresh) {
                Text("Refresh")
            }
            Button(onClick = { showAddQuestionDialog = true }) {
                Text("Add Question")
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

        when (questionsState) {
            is Resource.Success -> {
                QuestionList(questionsState.data ?: emptyList())
            }
            is Resource.Error -> {
                Text("Error loading questions: ${questionsState.message}", style = MaterialTheme.typography.body2)
            }
            is Resource.Loading -> {
                CircularProgressIndicator()
            }
            else -> {
                Text("No questions available", style = MaterialTheme.typography.body2)
            }
        }
    }
}

@Composable
fun QuestionList(questions: List<Question>) {
    LazyColumn {
        items(questions) { question ->
            QuestionListItem(question)
        }
    }
}

@Composable
fun QuestionListItem(question: Question) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = 2.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Question: ${question.content}", style = MaterialTheme.typography.h6)
            Text("Response: ${question.response}", style = MaterialTheme.typography.body1)
        }
    }
}

@Composable
fun AddQuestionDialog(onAddQuestion: (Question) -> Unit, onDismiss: () -> Unit) {
    var content by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Question") },
        text = {
            Column {
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") }
                )
                TextField(
                    value = response,
                    onValueChange = { response = it },
                    label = { Text("Response") }
                )
                TextField(
                    value = priority.toString(),
                    onValueChange = { priority = it.toIntOrNull() ?: 0 },
                    label = { Text("Priority") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddQuestion(Question(id = "", content = content, response = response, priority = priority))
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


