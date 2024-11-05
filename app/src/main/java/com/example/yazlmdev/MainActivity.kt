package com.example.yazlmdev
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.example.yazlmdev.adapters.NewMainActivity
import androidx.appcompat.app.AppCompatActivity;
class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Yeni arayüzün XML dosyasını ayarla
        val buttonBox1: Button = findViewById(R.id.buttonBox1)
        val buttonBox2: Button = findViewById(R.id.buttonBox2)
        buttonBox1.setOnClickListener {
            // Takvim sayfasını aç
            val intent = Intent(this, NewMainActivity::class.java)
            startActivity(intent)
        }
        buttonBox2.setOnClickListener {
            // Takvim sayfasını aç
            val intent = Intent(this, NotebookCoverActivity::class.java)
            startActivity(intent)
        }
    }
}