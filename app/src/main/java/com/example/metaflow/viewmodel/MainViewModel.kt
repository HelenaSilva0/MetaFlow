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
import com.example.metaflow.model.Mission
import com.example.metaflow.model.User
import com.example.metaflow.monitor.GoalMonitor
import java.util.Calendar

class MainViewModel(private val db: FBDatabase, private val monitor: GoalMonitor) : ViewModel(), FBDatabase.Listener {

    private val _goals = mutableStateListOf<Goal>()
    val goals get() = _goals.toList()

    private val _user = mutableStateOf<User?>(null)
    val user: User? get() = _user.value

    private val _ranking = mutableStateListOf<User>()
    val ranking get() = _ranking.toList()

    private val _missions = mutableStateListOf<Mission>()
    val missions get() = _missions.toList()

    init {
        db.setListener(this)
        db.getAllUsers { users ->
            _ranking.clear()
            _ranking.addAll(users.map { it.toUser() })
        }
        generateDailyMissions()
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

    fun updateUserProfile(newName: String) {
        val currentUser = _user.value ?: return
        if (newName.isBlank()) return
        
        val updatedUser = currentUser.copy(name = newName)
        db.updateUser(updatedUser.toFBUser())
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

    fun updateGoal(goal: Goal) {
        db.update(goal.toFBGoal())
    }

    fun toggleGoal(goal: Goal) {
        val now = System.currentTimeMillis()
        val updatedGoal = goal.copy(
            completed = !goal.completed,
            completedAt = if (!goal.completed) now else null
        )
        db.add(updatedGoal.toFBGoal())
        
        if (updatedGoal.completed) {
            updateGamificationStats(updatedGoal)
        } else {
            // Se desmarcar, apenas remove o XP base (simplificado)
            val currentXP = _user.value?.xp ?: 0
            val newXP = (currentXP - 50).coerceAtLeast(0)
            db.updateUserXP(newXP)
        }
    }

    private fun updateGamificationStats(goal: Goal) {
        val currentUser = _user.value ?: return
        val now = System.currentTimeMillis()
        
        // 1. Cálculo de Streak
        val lastActivity = currentUser.lastActivityDate
        val streak = calculateNewStreak(lastActivity, now, currentUser.streak)
        
        // 2. XP Dinâmico
        var xpGain = 50 // Base
        if (goal.priority == "Alta") xpGain += 25
        xpGain += (streak * 5).coerceAtMost(50) // Bônus de streak
        
        val newXP = currentUser.xp + xpGain
        val newTotalCompleted = currentUser.totalCompleted + 1
        
        // 3. Verificação de Insígnias
        val newBadges = currentUser.badges.toMutableList()
        checkAndAddBadges(newTotalCompleted, newBadges)
        
        val updatedUser = currentUser.copy(
            xp = newXP,
            streak = streak,
            lastActivityDate = now,
            totalCompleted = newTotalCompleted,
            badges = newBadges
        )
        
        db.updateUserStats(updatedUser.toFBUser())
        
        // 4. Atualizar Missões
        updateMissionProgress()
    }

    private fun calculateNewStreak(lastActivity: Long, now: Long, currentStreak: Int): Int {
        if (lastActivity == 0L) return 1
        
        val calLast = Calendar.getInstance().apply { timeInMillis = lastActivity }
        val calNow = Calendar.getInstance().apply { timeInMillis = now }
        
        // Mesma data
        if (calLast.get(Calendar.DAY_OF_YEAR) == calNow.get(Calendar.DAY_OF_YEAR) &&
            calLast.get(Calendar.YEAR) == calNow.get(Calendar.YEAR)) {
            return currentStreak
        }
        
        // Dia anterior
        calLast.add(Calendar.DAY_OF_YEAR, 1)
        if (calLast.get(Calendar.DAY_OF_YEAR) == calNow.get(Calendar.DAY_OF_YEAR) &&
            calLast.get(Calendar.YEAR) == calNow.get(Calendar.YEAR)) {
            return currentStreak + 1
        }
        
        return 1 // Reset
    }

    private fun checkAndAddBadges(totalCompleted: Int, badges: MutableList<String>) {
        if (totalCompleted >= 1 && !badges.contains("primeira_meta")) badges.add("primeira_meta")
        if (totalCompleted >= 10 && !badges.contains("bronze_meta")) badges.add("bronze_meta")
        if (totalCompleted >= 50 && !badges.contains("prata_meta")) badges.add("prata_meta")
        if (totalCompleted >= 100 && !badges.contains("ouro_meta")) badges.add("ouro_meta")
    }

    private fun generateDailyMissions() {
        _missions.clear()
        _missions.add(Mission("m1", "Explorador", "Complete 1 meta hoje", 20, targetCount = 1))
        _missions.add(Mission("m2", "Focado", "Complete 3 metas hoje", 60, targetCount = 3))
        _missions.add(Mission("m3", "Alta Prioridade", "Complete uma meta de alta prioridade", 40, targetCount = 1))
    }

    private fun updateMissionProgress() {
        val todayCompleted = getTodayGoals().size
        val highPriorityToday = getTodayGoals().count { it.priority == "Alta" }
        
        val updatedMissions = _missions.map { mission ->
            when (mission.id) {
                "m1" -> mission.copy(currentCount = todayCompleted, isCompleted = todayCompleted >= 1)
                "m2" -> mission.copy(currentCount = todayCompleted, isCompleted = todayCompleted >= 3)
                "m3" -> mission.copy(currentCount = highPriorityToday, isCompleted = highPriorityToday >= 1)
                else -> mission
            }
        }
        _missions.clear()
        _missions.addAll(updatedMissions)
    }

    override fun onUserLoaded(user: FBUser) {
        _user.value = user.toUser()
    }

    override fun onUserSignOut() {
        _user.value = null
        _goals.clear()
        _ranking.clear()
        monitor.cancelAll()
    }

    override fun onGoalAdded(goal: FBGoal) {
        val newGoal = goal.toGoal()
        if (_goals.none { it.id == newGoal.id }) {
            _goals.add(newGoal)
            monitor.updateGoal(newGoal)
        }
    }

    override fun onGoalUpdated(goal: FBGoal) {
        val updatedGoal = goal.toGoal()
        val index = _goals.indexOfFirst { it.id == updatedGoal.id }
        if (index != -1) {
            _goals[index] = updatedGoal
            monitor.updateGoal(updatedGoal)
        }
    }

    override fun onGoalRemoved(goal: FBGoal) {
        val removedGoal = goal.toGoal()
        _goals.removeIf { it.id == removedGoal.id }
        monitor.cancelGoal(removedGoal)
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

    // --- Lógica de Histórico ---

    fun getTodayGoals(): List<Goal> {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return _goals.filter { it.completed && (it.completedAt ?: 0L) >= todayStart }
            .sortedByDescending { it.completedAt }
    }

    fun getThisWeekGoals(): List<Goal> {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val weekStart = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
        }.timeInMillis

        return _goals.filter { 
            it.completed && 
            (it.completedAt ?: 0L) >= weekStart && 
            (it.completedAt ?: 0L) < todayStart 
        }.sortedByDescending { it.completedAt }
    }

    fun getThisMonthGoals(): List<Goal> {
        val weekStart = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
        }.timeInMillis

        val monthStart = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -30)
        }.timeInMillis

        return _goals.filter { 
            it.completed && 
            (it.completedAt ?: 0L) >= monthStart && 
            (it.completedAt ?: 0L) < weekStart 
        }.sortedByDescending { it.completedAt }
    }

    fun generateCommunity(onResult: (Boolean) -> Unit) {
        val fakeUsers = listOf(
            User("Ana Oliveira", "ana@flow.com", 3200),
            User("Bruno Santos", "bruno@flow.com", 2850),
            User("Carla Mendonça", "carla@flow.com", 2400),
            User("Diego Lima", "diego@flow.com", 1950),
            User("Elena Souza", "elena@flow.com", 1600),
            User("Fabio Rocha", "fabio@flow.com", 1200),
            User("Gabi Costa", "gabi@flow.com", 850),
            User("Hugo Silva", "hugo@flow.com", 500)
        )
        db.seedUsers(fakeUsers.map { it.toFBUser() }, onResult)
    }
}

class MainViewModelFactory(private val db: FBDatabase, private val monitor: GoalMonitor) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(db, monitor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
