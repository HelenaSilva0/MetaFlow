package com.example.metaflow.monitor

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.metaflow.model.Goal
import java.util.concurrent.TimeUnit

class GoalMonitor(context: Context) {
    private val wm = WorkManager.getInstance(context)
    private val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val TAG = "GoalMonitor"

    fun updateGoal(goal: Goal) {
        cancelGoal(goal)
        if (!goal.isMonitored) return

        Log.d(TAG, "Enqueuing monitoring for goal: ${goal.name}")
        val inputData = Data.Builder().putString("goal", goal.name).build()
        
        // Disparo imediato de teste (após 5 segundos)
        val testRequest = OneTimeWorkRequestBuilder<GoalWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .setInputData(inputData)
            .addTag("test_${goal.id}")
            .build()
        wm.enqueue(testRequest)
        Log.d(TAG, "OneTimeWorkRequest enqueued for testing")

        val request = PeriodicWorkRequestBuilder<GoalWorker>(
            repeatInterval = 15, repeatIntervalTimeUnit = TimeUnit.MINUTES
        ).setInitialDelay(
            duration = 10, timeUnit = TimeUnit.SECONDS
        ).setInputData(inputData)
            .addTag("periodic_${goal.id}")
            .build()

        wm.enqueueUniquePeriodicWork(
            goal.id,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
        Log.d(TAG, "UniquePeriodicWork enqueued")
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
