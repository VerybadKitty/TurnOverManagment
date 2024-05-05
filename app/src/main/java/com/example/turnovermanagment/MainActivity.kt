package com.example.turnovermanagment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.turnovermanagment.ui.TasksScreen
import com.example.turnovermanagment.ui.QuestionsScreen
import com.example.turnovermanagment.ui.ReviewsScreen
import com.example.turnovermanagment.ui.PropertyScreen
import androidx.compose.material.icons.filled.*
import com.example.turnovermanagment.ui.*
import com.example.turnovermanagment.Data.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create an instance of DatabaseManager
        val databaseManager = DatabaseManager()

        // Pass the databaseManager instance to each service
        val propertyService = PropertyService(databaseManager)
        val taskService = TaskService(databaseManager)
        val questionService = QuestionQueueService(databaseManager)
        val reviewService = ReviewService(databaseManager)
        val userService = UserService(databaseManager)

        setContent {
            MainScreen(propertyService, taskService, questionService, reviewService, userService)
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    propertyService: PropertyService,
    taskService: TaskService,
    questionService: QuestionQueueService,
    reviewService: ReviewService,
    userService: UserService
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) {
        NavHost(navController = navController, startDestination = Screen.Properties.route) {
            composable(Screen.Properties.route) { PropertyScreen(propertyService) }
            composable(Screen.Tasks.route) { TasksScreen(taskService) }
            composable(Screen.Questions.route) { QuestionsScreen(questionService) }
            composable(Screen.Reviews.route) { ReviewsScreen(reviewService) }
            composable(Screen.Users.route) { UsersScreen(userService) }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Properties,
        Screen.Tasks,
        Screen.Questions,
        Screen.Reviews,
        Screen.Users
    )
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

enum class Screen(val route: String, val icon: ImageVector, val label: String) {
    Properties("properties", Icons.Default.Home, "Properties"),
    Tasks("tasks", Icons.AutoMirrored.Filled.List, "Tasks"),
    Questions("questions", Icons.Default.Create, "Questions"),
    Reviews("reviews", Icons.Default.Star, "Reviews"),
    Users("users", Icons.Default.Person, "Users")
}

