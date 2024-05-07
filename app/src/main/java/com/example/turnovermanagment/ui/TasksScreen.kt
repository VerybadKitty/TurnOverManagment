package com.example.turnovermanagment.ui


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.turnovermanagment.Data.Task
import com.example.turnovermanagment.utils.Resource
import com.example.turnovermanagment.Data.TaskService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun TasksScreen(taskService: TaskService) {
    val tasksViewModel: TasksViewModel = viewModel(factory = ViewModelFactory(taskService))
    val tasksState by tasksViewModel.tasks.collectAsState()
    val isLoading by tasksViewModel.isLoading.collectAsState()
    val errorMessage by tasksViewModel.errorMessage.collectAsState()


    TasksContent(
        tasksState = tasksState,
        isLoading = isLoading,
        errorMessage = errorMessage,
        onRefresh = { tasksViewModel.loadTasks() },
        onCreateTask = { tasksViewModel.createTask(it) }
    )
}

@Composable
fun TasksContent(
    tasksState: Resource<List<Task>>,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit,
    onCreateTask: (Task) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {

        AddTaskDialog(onCreateTask = {
            onCreateTask(it)
            showDialog = false
        }, onDismiss = { showDialog = false })
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onRefresh) {
                Text("Refresh")
            }
            Button(onClick = { showDialog = true }) {
                Text("Create Task")
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

        when (tasksState) {
            is Resource.Success -> {
                TaskList(tasks = tasksState.data ?: emptyList())
            }
            is Resource.Error -> {
                Text("Error loading tasks: ${tasksState.message}", style = MaterialTheme.typography.body2)
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
fun TaskList(tasks: List<Task>) {
    LazyColumn {
        items(tasks) { task ->
            TaskListItem(task)
        }
    }
}

@Composable
fun TaskListItem(task: Task) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = 2.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Task: ${task.description}", style = MaterialTheme.typography.h6)
            Text("Due: ${task.dueDate}", style = MaterialTheme.typography.body1)
            Text("Priority: ${task.priority}", style = MaterialTheme.typography.body2)
            Text("Status: ${task.status}", style = MaterialTheme.typography.body2)
            task.assignedTo?.let {
                Text("Assigned to: $it", style = MaterialTheme.typography.body2)
            }
        }
    }
}
@Composable
fun AddTaskDialog(onCreateTask: (Task) -> Unit, onDismiss: () -> Unit) {
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Task") },
        text = {
            Column {
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
                TextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") }
                )
                TextField(
                    value = priority,
                    onValueChange = { priority = it },
                    label = { Text("Priority") }
                )
                TextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Status") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreateTask(Task(
                        id = "",
                        description = description,
                        dueDate = parseDate(dueDate),
                        priority = priority.toIntOrNull() ?: 1,
                        status = status,
                        assignedTo = null, // Optional: implement user assignment
                        relatedUnitId = "" // Optional: link to a specific unit if needed
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

fun parseDate(dateStr: String): Date {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.parse(dateStr) ?: Date()
}
