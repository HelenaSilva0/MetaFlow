package com.example.metaflow.monitor

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.metaflow.model.Goal
import java.util.Calendar
import java.util.concurrent.TimeUnit

class GoalMonitor(context: Context) {
    private val wm = WorkManager.getInstance(context)
    private val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val TAG = "GoalMonitor"

    fun updateGoal(goal: Goal) {
        cancelGoal(goal)
        if (!goal.isMonitored) return

        Log.d(TAG, "Enqueuing monitoring for goal: ${goal.name} (Time: ${goal.reminderTime}, Recurrence: ${goal.recurrence})")
        val inputData = Data.Builder().putString("goal", goal.name).build()
        
        val initialDelay = calculateInitialDelay(goal.reminderTime)
        Log.d(TAG, "Initial delay for ${goal.name}: ${initialDelay / 1000} seconds")

        when (goal.recurrence) {
            "Diário" -> {
                val request = PeriodicWorkRequestBuilder<GoalWorker>(24, TimeUnit.HOURS)
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag("periodic_${goal.id}")
                    .build()
                wm.enqueueUniquePeriodicWork(
                    goal.id,
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    request
                )
            }
            "Semanal" -> {
                val request = PeriodicWorkRequestBuilder<GoalWorker>(7, TimeUnit.DAYS)
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag("periodic_${goal.id}")
                    .build()
                wm.enqueueUniquePeriodicWork(
                    goal.id,
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    request
                )
            }
            else -> { // "Uma vez"
                val request = OneTimeWorkRequestBuilder<GoalWorker>()
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag("once_${goal.id}")
                    .build()
                wm.enqueueUniqueWork(
                    goal.id,
                    ExistingWorkPolicy.REPLACE,
                    request
                )
            }
        }
    }

    private fun calculateInitialDelay(time: String): Long {
        if (time == "--:--" || time.isBlank() || !time.contains(":")) {
            return 5000 // Default 5s if not valid
        }

        return try {
            val parts = time.split(":")
            val hour = parts[0].trim().toInt()
            val minute = parts[1].trim().toInt()

            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (target.before(now)) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }

            (target.timeInMillis - now.timeInMillis).coerceAtLeast(0)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing time: $time", e)
            5000
        }
    }

    fun cancelGoal(goal: Goal) {
        wm.cancelUniqueWork(goal.id)
        nm.cancel(goal.name.hashCode())
    }

    fun cancelAll() {
        wm.cancelAllWork()
        nm.cancelAll()
    }
}
