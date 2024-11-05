package com.example.yazlmdev

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class DiaryPageActivity : AppCompatActivity() {

    private lateinit var dateTextView: TextView
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private lateinit var editText: EditText
    private val dateList = generateDateList()
    private var currentPageIndex = 0
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_page)

        dateTextView = findViewById(R.id.dateTextView)
        previousButton = findViewById(R.id.previousButton)
        nextButton = findViewById(R.id.nextButton)
        editText = findViewById(R.id.editText)

        // SharedPreferences ayarları
        sharedPreferences = getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE)

        // İlk sayfayı göster
        showDateAndLoadText()

        // Önceki gün butonuna tıklama işlemi
        previousButton.setOnClickListener {
            if (currentPageIndex > 0) {
                saveCurrentText() // Mevcut sayfayı kaydediyoruz
                currentPageIndex--
                showDateAndLoadText()
            }
        }

        // Sonraki gün butonuna tıklama işlemi
        nextButton.setOnClickListener {
            if (currentPageIndex < dateList.size - 1) {
                saveCurrentText() // Mevcut sayfayı kaydediyoruz
                currentPageIndex++
                showDateAndLoadText()
            }
        }
    }

    // Geçerli tarih ve metni gösterir
    private fun showDateAndLoadText() {
        val date = dateList[currentPageIndex]
        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
        dateTextView.text = formattedDate

        // Kaydedilen metni yükle
        val savedText = sharedPreferences.getString(formattedDate, "")
        editText.setText(savedText)
    }

    // Mevcut sayfanın metnini kaydeder
    private fun saveCurrentText() {
        val date = dateList[currentPageIndex]
        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
        val textToSave = editText.text.toString()

        // Metni SharedPreferences içine kaydet
        sharedPreferences.edit().putString(formattedDate, textToSave).apply()
    }

    // Tarih listesini oluşturur
    private fun generateDateList(): List<Date> {
        val calendar = Calendar.getInstance()
        val dates = mutableListOf<Date>()

        for (i in 0 until 30) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return dates
    }

    override fun onPause() {
        super.onPause()
        // Etkinlik durdurulurken geçerli metni kaydedin
        saveCurrentText()
    }
}

