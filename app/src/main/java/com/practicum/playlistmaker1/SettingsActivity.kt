package com.practicum.playlistmaker1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonSettingsArrowBack = findViewById<ImageView>(R.id.settings_arrow_back)

        buttonSettingsArrowBack.setOnClickListener{
            val backSettingsIntent = Intent(this, MainActivity::class.java)
            startActivity(backSettingsIntent)
        }
    }
}