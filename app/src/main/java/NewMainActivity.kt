package com.example.yazlmdev.adapters
import android.app.AlarmManager
import android.widget.Toast
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.CalendarView
import com.example.yazlmdev.adapters.TextEntryActivity
import android.app.PendingIntent
import com.example.yazlmdev.NotificationReceiver
import com.example.yazlmdev.R
import java.util.Calendar

class NewMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_main)

        // Kanalı oluştur
        createNotificationChannel()

        val calendarView: CalendarView = findViewById(R.id.calendarView)

        // Tarih seçildiğinde olay dinleyicisi
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"

            val sharedPreferences = getSharedPreferences("CalendarNotes", MODE_PRIVATE)
            val note = sharedPreferences.getString(selectedDate, "")

            // TextEntryActivity'ye geçiş yap
            val intent = Intent(this, TextEntryActivity::class.java)
            intent.putExtra("selectedDate", selectedDate)
            startActivity(intent)
            scheduleNotification(year, month, dayOfMonth)
            // Bildirim gönder
          /* sendNotification() // Burada bildirim gönderimi yapılacak*/
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "reminder_channel_id"
            val channelName = "Hatırlatıcı Bildirimleri"
            val channelDescription = "Hatırlatıcı bildirimleri için kanal"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /*private fun sendNotification() {
        val notificationId = 1
        val channelId = "reminder_channel_id"

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.reminder) // Burada uygun bir ikon kullanın
            .setContentTitle("Hatırlatıcı")
            .setContentText("Görevlerinizden biri için hatırlatıcı!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }*/
    private fun scheduleNotification(year: Int, month: Int, dayOfMonth: Int) {
        // Check if the app can schedule exact alarms
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Show a dialog or a toast to inform the user
            Toast.makeText(this, "Please enable exact alarm permissions in settings.", Toast.LENGTH_LONG).show()
            return
        }

        // Set the time for the notification (e.g., 9 AM)
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth, 9, 0, 0) // Set the desired hour and minute
        calendar.set(Calendar.MILLISECOND, 0)

        // Create an intent for the notification
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        try {
            // Set the alarm
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            // Handle the SecurityException
            Toast.makeText(this, "SecurityException: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

}


