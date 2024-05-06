package com.example.turnovermanagment.Data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.util.Date


data class User(
    @Exclude var id: String = "",
    @PropertyName("name") var name: String = "",
    @PropertyName("email") var email: String = "",
    @PropertyName("role") var role: String = ""
)

data class Property(
    @Exclude var id: String = "",
    @PropertyName("address") var address: String = "",
    @PropertyName("numberOfUnits") var numberOfUnits: Int = 0,
    @PropertyName("createdAt") var createdAt: Date? = null,
    @PropertyName("updatedAt") var updatedAt: Date? = null
)

data class PropertyUnit(
    @Exclude var id: String = "",
    @PropertyName("propertyId") var propertyId: String = "",
    @PropertyName("unitNumber") var unitNumber: String = "",
    @PropertyName("condition") var condition: String = "",
    @PropertyName("hasReview") var hasReview: Boolean = false
)

data class Task(
    @Exclude var id: String = "",
    @PropertyName("description") var description: String = "",
    @PropertyName("assignedTo") var assignedTo: String? = null,
    @PropertyName("priority") var priority: Int = 0,
    @PropertyName("status") var status: String = "",
    @PropertyName("dueDate") var dueDate: Date = Date(),
    @PropertyName("relatedUnitId") var relatedUnitId: String = ""
)

data class UnitReview(
    @Exclude var id: String = "",
    @PropertyName("unitId") var unitId: String = "",
    @PropertyName("reviewerId") var reviewerId: String = "",
    @PropertyName("dateReviewed") var dateReviewed: Long = System.currentTimeMillis(),
    @PropertyName("overallCondition") var overallCondition: String = "",
    @PropertyName("observations") var observations: List<Observation> = emptyList(),
    @PropertyName("photosUrls") var photosUrls: List<String> = emptyList(),
    var reviewText: String = ""
)

data class Observation(
    @PropertyName("area") var area: String = "",
    @PropertyName("issueType") var issueType: String = "",
    @PropertyName("description") var description: String = "",
    @PropertyName("severity") var severity: Severity = Severity.LOW
)

enum class Severity {
    LOW, MEDIUM, HIGH, CRITICAL
}

data class Question(
    @Exclude var id: String = "",
    @PropertyName("content") var content: String = "",
    @PropertyName("response") var response: String = "",
    @PropertyName("priority") var priority: Int = 0,
    @PropertyName("archived") var archived: Boolean = false
)

