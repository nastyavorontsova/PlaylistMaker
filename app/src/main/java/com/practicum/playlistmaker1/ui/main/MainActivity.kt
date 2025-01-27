package com.practicum.playlistmaker1.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.practicum.playlistmaker1.R
import com.practicum.playlistmaker1.ui.media.MediaActivity
import com.practicum.playlistmaker1.ui.settings.SettingsActivity
import com.practicum.playlistmaker1.ui.track.SearchActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearchMain = findViewById<Button>(R.id.search_main)
        val buttonMediaMain = findViewById<Button>(R.id.media_main)
        val buttonSettingsMain = findViewById<Button>(R.id.settings_main)

        val searchIntent = Intent(this, SearchActivity::class.java)

        val buttonClickListener: View.OnClickListener = View.OnClickListener { startActivity(searchIntent) }

        buttonSearchMain.setOnClickListener(buttonClickListener)

        buttonMediaMain.setOnClickListener {
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        buttonSettingsMain.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}
