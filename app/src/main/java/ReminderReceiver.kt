package com.example.yazlmdev

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskDescription = intent.getStringExtra("TASK_DESCRIPTION")
        showNotification(context, taskDescription)
    }

    private fun showNotification(context: Context, taskDescription: String?) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = taskDescription?.hashCode() ?: 0

        // Bildirim kanalı oluşturma (API 26 ve üstü için)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = "reminder_channel"
            val channelName = "Hatırlatıcılar"
            val channelDescription = "Görev hatırlatıcıları için kanal"

            // Kanala ait bildirim kanalını oluştur
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Bildirim oluşturma
        val builder = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.reminder) // İkonu buraya koyun
            .setContentTitle("Görev Hatırlatıcısı")
            .setContentText(taskDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Bildirimi göster


        notificationManager.notify(notificationId, builder.build())
    }
}
