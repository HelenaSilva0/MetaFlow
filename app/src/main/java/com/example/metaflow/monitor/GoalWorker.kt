package com.example.metaflow.monitor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.metaflow.MainActivity
import com.example.metaflow.R

class GoalWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    companion object {
        private const val CHANNEL_ID: String = "META_FLOW_APP"
        private const val TAG = "GoalWorker"
    }

    override fun doWork(): Result {
        Log.d(TAG, "Worker started")
        val goalName = inputData.getString("goal")
        if (goalName == null) {
            Log.e(TAG, "Goal name is null")
            return Result.failure()
        }
        
        Log.d(TAG, "Showing notification for goal: $goalName")
        showNotification(goalName)
        return Result.success()
    }

    private fun showNotification(goalName: String) {
        val newIntent = Intent(this.applicationContext, MainActivity::class.java)
        newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        newIntent.putExtra("goal", goalName)

        val pendingIntent = PendingIntent.getActivity(
            this.applicationContext,
            goalName.hashCode(),
            newIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        createNotificationChannel()

        val builder = NotificationCompat.Builder(this.applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(goalName)
            .setContentText("Lembrete: Como está o progresso da sua meta?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager: NotificationManager =
            this.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        Log.d(TAG, "Calling notificationManager.notify")
        notificationManager.notify(goalName.hashCode(), builder.build())
    }

    private fun createNotificationChannel() {
        Log.d(TAG, "Creating notification channel")
        val name = "MetaFlow"
        val descriptionText = "MetaFlow Goal Reminders"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            this.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        Log.d(TAG, "Notification channel created")
    }
}
