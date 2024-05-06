package com.example.turnovermanagment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.turnovermanagment.Data.User
import com.example.turnovermanagment.utils.Resource
import com.example.turnovermanagment.Data.UserService

@Composable
fun UsersScreen(userService: UserService) {
    val userViewModel: UserViewModel = viewModel(factory = ViewModelFactory(userService))
    val usersState by userViewModel.users.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val errorMessage by userViewModel.errorMessage.collectAsState()

    UsersContent(
        usersState = usersState,
        isLoading = isLoading,
        errorMessage = errorMessage,
        onRefresh = { userViewModel.loadUsers() },
        onAddUser = { userViewModel.addUser(it) }
    )
}


@Composable
fun UsersContent(
    usersState: Resource<List<User>>,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit,
    onAddUser: (User) -> Unit
) {
    var showAddUserDialog by remember { mutableStateOf(false) }

    if (showAddUserDialog) {
        AddUserDialog(onAddUser = {
            onAddUser(it)
            showAddUserDialog = false
        }, onDismiss = { showAddUserDialog = false })
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onRefresh) {
                Text("Refresh")
            }
            Button(onClick = { showAddUserDialog = true }) {
                Text("Add User")
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

        when (usersState) {
            is Resource.Success -> {
                if (usersState.data.isNullOrEmpty()) {
                    Text("No users available", style = MaterialTheme.typography.body2)
                } else {
                    UserList(usersState.data)
                }
            }
            is Resource.Error -> Text("Error loading users: ${usersState.message}", style = MaterialTheme.typography.body2)
            is Resource.Loading -> CircularProgressIndicator()
            else -> Text("Unexpected state", style = MaterialTheme.typography.body2)
        }
    }
}

@Composable
fun UserList(users: List<User>) {
    LazyColumn {
        items(users) { user ->
            UserListItem(user)
        }
    }
}

@Composable
fun UserListItem(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Name: ${user.name}", style = MaterialTheme.typography.h6)
            Text("Email: ${user.email}", style = MaterialTheme.typography.body1)
        }
    }
}
@Composable
fun AddUserDialog(onAddUser: (User) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New User") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
                TextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Role") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddUser(User(id = "", name = name, email = email, role = role)) // assuming IDs are generated server-side or by some function
                    onDismiss()
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
