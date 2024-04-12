package com.example.turnovermanagment.Data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.util.Date

data class User(
    @Exclude val id: String,
    @PropertyName("name") val name: String,
    @PropertyName("email") val email: String,
    @PropertyName("role") val role: String
)

data class Property(
    @Exclude val id: String,
    @PropertyName("address") val address: String,
    @PropertyName("numberOfUnits") val numberOfUnits: Int,
    @PropertyName("createdAt") val createdAt: Date? = null,
    @PropertyName("updatedAt") val updatedAt: Date? = null
)

data class Unit(
    @Exclude val id: String,
    @PropertyName("propertyId") val propertyId: String,
    @PropertyName("unitNumber") val unitNumber: String,
    @PropertyName("condition") val condition: String
)

data class Task(
    @Exclude val id: String,
    @PropertyName("description") val description: String,
    @PropertyName("assignedTo") val assignedTo: String?,
    @PropertyName("priority") val priority: Int,
    @PropertyName("status") val status: String,
    @PropertyName("dueDate") val dueDate: Date,
    @PropertyName("relatedUnitId") val relatedUnitId: String
)

data class UnitReview(
    @Exclude val id: String,
    @PropertyName("unitId") val unitId: String,
    @PropertyName("reviewerId") val reviewerId: String,
    @PropertyName("dateReviewed") val dateReviewed: Date,
    @PropertyName("overallCondition") val overallCondition: String,
    @PropertyName("observations") val observations: List<Observation>,
    @PropertyName("photosUrls") val photosUrls: List<String>
)

data class Observation(
    @PropertyName("area") val area: String,
    @PropertyName("issueType") val issueType: String,
    @PropertyName("description") val description: String,
    @PropertyName("severity") val severity: Severity
)

enum class Severity {
    LOW, MEDIUM, HIGH, CRITICAL
}


