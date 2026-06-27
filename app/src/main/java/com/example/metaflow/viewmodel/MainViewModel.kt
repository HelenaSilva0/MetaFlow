package com.example.metaflow.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.metaflow.db.fb.FBDatabase
import com.example.metaflow.db.fb.FBGoal
import com.example.metaflow.db.fb.FBUser
import com.example.metaflow.db.fb.toFBGoal
import com.example.metaflow.model.Goal
import com.example.metaflow.model.User

class MainViewModel(private val db: FBDatabase) : ViewModel(), FBDatabase.Listener {

    private val _goals = mutableStateListOf<Goal>()
    val goals get() = _goals.toList()

    private val _user = mutableStateOf<User?>(null)
    val user: User? get() = _user.value

    init {
        db.setListener(this)
    }

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

        db.add(newGoal.toFBGoal())
    }

    fun removeGoal(goal: Goal) {
        db.remove(goal.toFBGoal())
    }

    fun toggleGoal(goal: Goal) {
        // Para o toggleGoal funcionar com Firestore, precisaríamos de um update no FBDatabase
        // Por enquanto, vamos atualizar localmente ou adicionar update no FBDatabase se necessário.
        // Seguindo a Prática 06, focamos em add/remove.
        val updatedGoal = goal.copy(completed = !goal.completed)
        db.add(updatedGoal.toFBGoal()) // O set() no Firestore com mesmo ID sobrescreve
    }

    override fun onUserLoaded(user: FBUser) {
        _user.value = user.toUser()
    }

    override fun onUserSignOut() {
        _user.value = null
        _goals.clear()
    }

    override fun onGoalAdded(goal: FBGoal) {
        val newGoal = goal.toGoal()
        if (_goals.none { it.name == newGoal.name }) {
            _goals.add(newGoal)
        }
    }

    override fun onGoalUpdated(goal: FBGoal) {
        val updatedGoal = goal.toGoal()
        val index = _goals.indexOfFirst { it.name == updatedGoal.name }
        if (index != -1) {
            _goals[index] = updatedGoal
        }
    }

    override fun onGoalRemoved(goal: FBGoal) {
        val removedGoal = goal.toGoal()
        _goals.removeIf { it.name == removedGoal.name }
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

class MainViewModelFactory(private val db: FBDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
