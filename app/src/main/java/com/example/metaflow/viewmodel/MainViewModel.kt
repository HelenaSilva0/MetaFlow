package com.example.metaflow.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.metaflow.db.fb.FBDatabase
import com.example.metaflow.db.fb.FBGoal
import com.example.metaflow.db.fb.FBUser
import com.example.metaflow.db.fb.toFBGoal
import com.example.metaflow.db.fb.toFBUser
import com.example.metaflow.model.Goal
import com.example.metaflow.model.User

class MainViewModel(private val db: FBDatabase) : ViewModel(), FBDatabase.Listener {

    private val _goals = mutableStateListOf<Goal>()
    val goals get() = _goals.toList()

    private val _user = mutableStateOf<User?>(null)
    val user: User? get() = _user.value

    private val _ranking = mutableStateListOf<User>()
    val ranking get() = _ranking.toList()

    init {
        db.setListener(this)
        db.getAllUsers { users ->
            _ranking.clear()
            _ranking.addAll(users.map { it.toUser() })
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        db.login(email, password, onResult)
    }

    fun register(name: String, email: String, password: String, onResult: (Boolean) -> Unit) {
        db.registerAuth(email, password) { success ->
            if (success) {
                db.register(User(name, email).toFBUser())
            }
            onResult(success)
        }
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
            id = java.util.UUID.randomUUID().toString(),
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
        val now = System.currentTimeMillis()
        val updatedGoal = goal.copy(
            completed = !goal.completed,
            completedAt = if (!goal.completed) now else null
        )
        db.add(updatedGoal.toFBGoal())
        
        // Update XP: +50 for completion, -50 if uncompleted
        val xpChange = if (updatedGoal.completed) 50 else -50
        val currentXP = _user.value?.xp ?: 0
        db.updateUserXP(currentXP + xpChange)
    }

    override fun onUserLoaded(user: FBUser) {
        _user.value = user.toUser()
    }

    override fun onUserSignOut() {
        _user.value = null
        _goals.clear()
        _ranking.clear()
    }

    override fun onGoalAdded(goal: FBGoal) {
        val newGoal = goal.toGoal()
        if (_goals.none { it.id == newGoal.id }) {
            _goals.add(newGoal)
        }
    }

    override fun onGoalUpdated(goal: FBGoal) {
        val updatedGoal = goal.toGoal()
        val index = _goals.indexOfFirst { it.id == updatedGoal.id }
        if (index != -1) {
            _goals[index] = updatedGoal
        }
    }

    override fun onGoalRemoved(goal: FBGoal) {
        val removedGoal = goal.toGoal()
        _goals.removeIf { it.id == removedGoal.id }
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
        return _user.value?.xp ?: 0
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
