package com.example.metaflow.model

data class Goal(
    val id: String,
    val name: String,
    val category: String,
    val reminderTime: String,
    val priority: String,
    val recurrence: String = "Uma vez",
    val deadline: String = "",
    val location: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val completed: Boolean = false,
    val completedAt: Long? = null
)
