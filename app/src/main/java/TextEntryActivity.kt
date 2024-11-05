/*package com.example.yazlmdev.adapters

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.yazlmdev.R

class TextEntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_entry)

        val textViewDate = findViewById<TextView>(R.id.dateTextView)
        val editTextContent = findViewById<EditText>(R.id.editText)
        val buttonSave = findViewById<Button>(R.id.buttonSave)

        // Get the selected date from the intent
        val selectedDate = intent.getStringExtra("selectedDate") ?: ""
        textViewDate.text = selectedDate

        // Load saved text for the selected date
        val sharedPreferences = getSharedPreferences("CalendarNotes", Context.MODE_PRIVATE)
        editTextContent.setText(sharedPreferences.getString(selectedDate, ""))

        // Save button click
        buttonSave.setOnClickListener {
            val content = editTextContent.text.toString()
            sharedPreferences.edit().putString(selectedDate, content).apply()
            finish() // Close the activity after saving
        }
    }
}*/
package com.example.yazlmdev.adapters

import android.app.AlertDialog
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yazlmdev.R
import com.example.yazlmdev.ReminderReceiver
import android.os.Build
import android.os.PowerManager
import android.net.Uri


class TextEntryActivity : AppCompatActivity() {
    private lateinit var taskEditText: EditText
    private lateinit var setReminderButton: Button
    private lateinit var dateTextView: TextView
    private lateinit var saveButton: Button
    private lateinit var stickerButton: Button
    private var selectedInterval: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_entry)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
        // Arayüz bileşenlerini tanımlayın
        taskEditText = findViewById(R.id.editText) // Görev girişi için EditText
        setReminderButton = findViewById(R.id.setReminderButton) // Hatırlatıcıyı ayarlama butonu
        dateTextView = findViewById(R.id.dateTextView) // Seçilen tarihi göstermek için TextView
        saveButton = findViewById(R.id.buttonSave) // Kaydetme butonu


        // Seçilen tarihi intent'ten alın ve gösterin
        val selectedDate = intent.getStringExtra("selectedDate") ?: ""
        dateTextView.text = selectedDate

        // Seçilen tarih için kaydedilmiş metni yükleyin
        val sharedPreferences = getSharedPreferences("CalendarNotes", Context.MODE_PRIVATE)
        taskEditText.setText(sharedPreferences.getString(selectedDate, ""))

        // Kaydetme butonuna tıklama olayını ayarlayın
        saveButton.setOnClickListener {
            val content = taskEditText.text.toString()
            sharedPreferences.edit().putString(selectedDate, content).apply()
            Toast.makeText(this, "Görev kaydedildi!", Toast.LENGTH_SHORT).show()
            saveTaskAndSetReminder(selectedDate)
            finish() // Kaydettikten sonra aktiviteyi kapat
        }

        // Hatırlatıcıyı ayarlama butonuna tıklama olayını ayarlayın
        setReminderButton.setOnClickListener {
            setReminder(selectedDate) // Hatırlatıcı ayarları için tarihi geçirin
        }
        stickerButton = findViewById(R.id.stickerButton) // Sticker butonunu tanımlayın

        stickerButton.setOnClickListener {
            showStickerDialog() // Sticker seçme dialogunu göster
        }
    }

    private fun setReminder(selectedDate: String) {
        val taskDescription = taskEditText.text.toString()
        if (taskDescription.isNotEmpty()) {
            // Kullanıcıya süre seçeneklerini sunun
            val options = arrayOf("2 dakika sonra", "30 dakika sonra", "1 saat sonra")
            val timeIntervals = arrayOf(1 * 60 * 1000L, 30 * 60 * 1000L, 60 * 60 * 1000L) // milisaniye olarak

            AlertDialog.Builder(this)
                .setTitle("Hatırlatıcı Süresi Seçin")
                .setItems(options) { _, which ->
                    selectedInterval = timeIntervals[which] // Seçilen süreyi sakla
                    Toast.makeText(this, "Hatırlatıcı ${options[which]} olarak ayarlandı.", Toast.LENGTH_SHORT).show()

                }
                .show()
        } else {
            Toast.makeText(this, "Görev açıklaması boş olamaz!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveTaskAndSetReminder(selectedDate: String) {
        val taskDescription = taskEditText.text.toString()
        val sharedPreferences = getSharedPreferences("CalendarNotes", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(selectedDate, taskDescription).apply()
        Toast.makeText(this, "Görev kaydedildi!", Toast.LENGTH_SHORT).show()

        // Hatırlatıcıyı ayarlamak için süre seçildi mi kontrol edin
        selectedInterval?.let { interval ->
            scheduleReminder(interval, selectedDate, taskDescription)
        } ?: Toast.makeText(this, "Hatırlatıcı süresi seçilmedi.", Toast.LENGTH_SHORT).show()

        finish() // Kaydettikten sonra aktiviteyi kapat
    }

    private fun scheduleReminder(interval: Long, selectedDate: String, taskDescription: String) {
        val reminderTime = System.currentTimeMillis() + interval

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("TASK_DESCRIPTION", taskDescription)
            putExtra("SELECTED_DATE", selectedDate)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            taskDescription.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Alarmı ayarlayın
        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent)
            Toast.makeText(this, "Hatırlatıcı ayarlandı: ${interval / 60000} dakika sonra", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            // İzin eksikse kullanıcıyı bilgilendirin ve ayarlara yönlendirin
            Toast.makeText(this, "Tam hatırlatıcı ayarı yapılamıyor. Lütfen izinleri etkinleştirin.", Toast.LENGTH_LONG).show()
            val permissionIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(permissionIntent)
        }
    }
    private fun showStickerDialog() {
        val stickers = arrayOf("😊", "🎉", "💡", "🚀") // Sticker seçenekleri
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Bir sticker seçin")

        builder.setItems(stickers) { _, which ->
            val selectedSticker = stickers[which]
            taskEditText.append(selectedSticker) // Seçilen sticker'ı EditText'e ekleyin
        }

        builder.show()
    }
}