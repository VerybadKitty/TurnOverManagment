package com.example.turnovermanagment.Data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseManager {

    private val db = FirebaseFirestore.getInstance()

    // User Management
    fun addUser(user: User, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("Users").add(user).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun updateUser(userId: String, updates: Map<String, Any>) {
        db.collection("Users").document(userId).update(updates)
            .addOnSuccessListener { println("User updated successfully!") }
            .addOnFailureListener { e -> println("Error updating user: ${e.message}") }
    }

    fun deleteUser(userId: String) {
        db.collection("Users").document(userId).delete()
            .addOnSuccessListener { println("User deleted successfully!") }
            .addOnFailureListener { e -> println("Error deleting user: ${e.message}") }
    }

    fun getUsers(onSuccess: (List<User>) -> Unit, onError: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = db.collection("Users").get().await()
                val users = result.documents.mapNotNull { it.toObject(User::class.java) }
                onSuccess(users)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    // Property Management
    fun addProperty(property: Property, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("Properties").add(property).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun getProperty(propertyId: String, onPropertyReceived: (Property?) -> Unit) {
        db.collection("Properties").document(propertyId).get()
            .addOnSuccessListener { documentSnapshot ->
                val property = documentSnapshot.toObject(Property::class.java)
                onPropertyReceived(property)
            }
            .addOnFailureListener { e ->
                println("Error getting property: ${e.message}")
                onPropertyReceived(null)
            }
    }

    // Unit Management
    fun addUnit(
        propertyId: String,
        unit: Unit,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("Properties").document(propertyId)
                    .collection("Units").add(unit).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    // Task Management
    fun addTask(task: Task, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("Tasks").add(task).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun updateTask(taskId: String, updates: Map<String, Any>) {
        db.collection("Tasks").document(taskId).update(updates)
            .addOnSuccessListener { println("Task updated successfully!") }
            .addOnFailureListener { e -> println("Error updating task: ${e.message}") }
    }

    fun assignTask(taskId: String, userId: String) {
        updateTask(taskId, mapOf("assignedTo" to userId))
    }

    // Review Management
    fun addReview(review: UnitReview, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("UnitReviews").add(review).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }


    fun addQuestion(question: Question, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("Questions").add(question).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun getQuestions(onSuccess: (List<Question>) -> Unit, onError: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = db.collection("Questions").get().await()
                val questions = result.documents.mapNotNull { it.toObject(Question::class.java) }
                onSuccess(questions)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun updateQuestion(
        questionId: String,
        response: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("Questions").document(questionId).update("response", response).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun updateQuestionPriority(
        questionId: String,
        priority: Int,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("Questions").document(questionId).update("priority", priority).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun archiveQuestion(questionId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("Questions").document(questionId).update("archived", true).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
