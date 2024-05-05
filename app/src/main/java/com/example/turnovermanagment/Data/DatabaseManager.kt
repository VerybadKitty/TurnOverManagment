package com.example.turnovermanagment.Data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await


class DatabaseManager {

    private val db = FirebaseFirestore.getInstance()

    // User Management
    suspend fun addUser(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("Users").add(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(userId: String, updates: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("Users").document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("Users").document(userId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUsers(): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("Users").get().await()
            Result.success(snapshot.toObjects(User::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Task Management
    suspend fun addTask(task: Task): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("Tasks").add(task).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTasks(): Result<List<Task>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("Tasks").get().await()
            Result.success(snapshot.toObjects(Task::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(taskId: String, updates: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("Tasks").document(taskId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("Tasks").document(taskId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun assignTask(taskId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("Tasks").document(taskId).update("assignedTo", userId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun completeTask(taskId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updates = mapOf("status" to "Completed")
            db.collection("Tasks").document(taskId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // Property Management
    suspend fun addProperty(property: Property): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("Properties").add(property).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProperties(): Result<List<Property>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("Properties").get().await()
            Result.success(snapshot.toObjects(Property::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProperty(propertyId: String, updates: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("Properties").document(propertyId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProperty(propertyId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("Properties").document(propertyId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getProperty(propertyId: String): Result<Property?> = withContext(Dispatchers.IO) {
        try {
            val documentSnapshot = db.collection("Properties").document(propertyId).get().await()
            val property = documentSnapshot.toObject(Property::class.java)?.let {
                it.id = propertyId // Ensure the ID is set correctly after retrieval
                it
            }
            Result.success(property)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getUnitsForProperty(propertyId: String): Result<List<PropertyUnit>> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = db.collection("Properties").document(propertyId).collection("Units").get().await()
            val units = querySnapshot.toObjects(PropertyUnit::class.java)
            units.forEach { it.propertyId = propertyId }  // Ensure each unit has the correct property ID set
            Result.success(units)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Function to add a new unit to a specific property
    suspend fun addUnit(propertyId: String, unit: PropertyUnit): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("Properties").document(propertyId).collection("Units").add(unit).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Review Management
    suspend fun addReview(review: UnitReview): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("UnitReviews").add(review).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviews(): Result<List<UnitReview>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("UnitReviews").get().await()
            Result.success(snapshot.toObjects(UnitReview::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateReview(reviewId: String, reviewUpdates: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("UnitReviews").document(reviewId).update(reviewUpdates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReview(reviewId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("UnitReviews").document(reviewId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Question Management
    suspend fun addQuestion(question: Question): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("Questions").add(question).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQuestions(): Result<List<Question>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("Questions").get().await()
            Result.success(snapshot.toObjects(Question::class.java))
        } catch (e: Exception) {
        Result.failure(e)
    }
    }

    suspend fun updateQuestion(questionId: String, updates: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("Questions").document(questionId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun archiveQuestion(questionId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("Questions").document(questionId).update("archived", true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
