package com.example.metaflow.model

data class Mission(
    val id: String,
    val title: String,
    val description: String,
    val points: Int,
    val isCompleted: Boolean = false,
    val targetCount: Int = 1,
    val currentCount: Int = 0
)
