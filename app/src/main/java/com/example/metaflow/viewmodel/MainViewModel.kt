package com.example.metaflow.viewmodel

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.metaflow.model.Goal

class MainViewModel : ViewModel() {

    private val _goals = listOf(
        Goal(
            id = 1,
            name = "Beber água",
            category = "Saúde",
            reminderTime = "08:00",
            priority = "Alta",
            recurrence = "Diário"
        ),
        Goal(
            id = 2,
            name = "Estudar 30 minutos",
            category = "Estudos",
            reminderTime = "19:00",
            priority = "Média",
            recurrence = "Seg a Sex"
        ),
        Goal(
            id = 3,
            name = "Fazer exercício",
            category = "Rotina",
            reminderTime = "17:30",
            priority = "Alta",
            recurrence = "Diário"
        )
    ).toMutableStateList()

    val goals
        get() = _goals.toList()

    fun addGoal(
        name: String,
        category: String,
        reminderTime: String,
        priority: String,
        recurrence: String = "Uma vez",
        deadline: String = "",
        location: String = "",
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        val newGoal = Goal(
            id = _goals.size + 1,
            name = name,
            category = category,
            reminderTime = reminderTime,
            priority = priority,
            recurrence = recurrence,
            deadline = deadline,
            location = location,
            latitude = latitude,
            longitude = longitude
        )

        _goals.add(newGoal)
    }

    fun removeGoal(goal: Goal) {
        _goals.remove(goal)
    }

    fun toggleGoal(goal: Goal) {
        val index = _goals.indexOf(goal)

        if (index != -1) {
            _goals[index] = goal.copy(completed = !goal.completed)
        }
    }

    fun completedCount(): Int {
        return _goals.count { it.completed }
    }

    fun totalCount(): Int {
        return _goals.size
    }

    fun progressPercent(): Int {
        if (_goals.isEmpty()) return 0
        return (completedCount() * 100) / _goals.size
    }

    fun xpPoints(): Int {
        return completedCount() * 50
    }
}
