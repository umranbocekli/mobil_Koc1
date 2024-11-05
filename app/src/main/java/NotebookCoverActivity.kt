package com.example.yazlmdev

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class NotebookCoverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notebook_cover)

        val openPagesButton: Button = findViewById(R.id.openPagesButton)
        openPagesButton.setOnClickListener {
            // Sayfa aktivitesini aç
            val intent = Intent(this, DiaryPageActivity::class.java)
            startActivity(intent)
        }
    }
}
