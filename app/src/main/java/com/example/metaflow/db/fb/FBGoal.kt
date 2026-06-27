package com.example.metaflow.db.fb

import com.example.metaflow.model.Goal

class FBGoal {
    var id: Int = 0
    var name: String? = null
    var category: String? = null
    var reminderTime: String? = null
    var priority: String? = null
    var recurrence: String? = null
    var deadline: String? = null
    var location: String? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var completed: Boolean = false

    fun toGoal(): Goal {
        return Goal(
            id = id,
            name = name ?: "",
            category = category ?: "Geral",
            reminderTime = reminderTime ?: "--:--",
            priority = priority ?: "Média",
            recurrence = recurrence ?: "Uma vez",
            deadline = deadline ?: "",
            location = location ?: "",
            latitude = latitude,
            longitude = longitude,
            completed = completed
        )
    }
}

fun Goal.toFBGoal(): FBGoal {
    val fbGoal = FBGoal()
    fbGoal.id = this.id
    fbGoal.name = this.name
    fbGoal.category = this.category
    fbGoal.reminderTime = this.reminderTime
    fbGoal.priority = this.priority
    fbGoal.recurrence = this.recurrence
    fbGoal.deadline = this.deadline
    fbGoal.location = this.location
    fbGoal.latitude = this.latitude
    fbGoal.longitude = this.longitude
    fbGoal.completed = this.completed
    return fbGoal
}
