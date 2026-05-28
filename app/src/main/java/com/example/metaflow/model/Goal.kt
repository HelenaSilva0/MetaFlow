package com.example.metaflow.model

data class Goal(
    val id: Int,
    val name: String,
    val category: String,
    val reminderTime: String,
    val priority: String,
    val completed: Boolean = false
)