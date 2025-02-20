package com.practicum.playlistmaker1.settings.ui

import android.widget.ImageView
import android.widget.LinearLayout
import com.practicum.playlistmaker1.creator.Creator
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker1.R
import com.practicum.playlistmaker1.creator.App

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Инициализация ViewModel
        val themeManager = Creator.provideThemeManager(this)
        val sharingManager = Creator.provideSharingManager(this)
        val app = application as App
        val viewModelFactory = SettingsViewModelFactory(themeManager, sharingManager, app)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)

        // Инициализация View элементов
        val buttonSettingsArrowBack: ImageView = findViewById(R.id.settings_arrow_back)
        val themeSwitcher: SwitchMaterial = findViewById(R.id.themeSwitcher)
        val shareButton: LinearLayout = findViewById(R.id.share_button)
        val supportButton: LinearLayout = findViewById(R.id.support_button)
        val privacyAgreementButton: LinearLayout = findViewById(R.id.privacy_agreement_button)

        // Установка начального состояния переключателя темы
        themeSwitcher.isChecked = viewModel.isDarkThemeEnabled()

        // Обработка нажатия на кнопку "Назад"
        buttonSettingsArrowBack.setOnClickListener {
            finish()
        }

        // Обработка изменения темы
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
            restartActivity() // Перезапуск текущей Activity
        }

        // Обработка нажатия на кнопку "Поделиться"
        shareButton.setOnClickListener {
            viewModel.shareApp()
        }

        // Обработка нажатия на кнопку "Написать в поддержку"
        supportButton.setOnClickListener {
            viewModel.openSupport()
        }

        // Обработка нажатия на кнопку "Пользовательское соглашение"
        privacyAgreementButton.setOnClickListener {
            viewModel.openPrivacyAgreement()
        }
    }

    private fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }
}
