package com.example.metaflow.model

data class User(
    val name: String,
    val email: String,
    val xp: Int = 0,
    val streak: Int = 0,
    val lastActivityDate: Long = 0L,
    val badges: List<String> = emptyList(),
    val totalCompleted: Int = 0,
    val profilePic: String? = null,
    val theme: String = "Sistema"
)
