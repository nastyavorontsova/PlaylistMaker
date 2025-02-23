package com.practicum.playlistmaker1.settings.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker1.R
import com.practicum.playlistmaker1.creator.Creator

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
        val viewModelFactory = SettingsViewModelFactory(themeManager, sharingManager)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)

        // Инициализация View элементов
        val buttonSettingsArrowBack: ImageView = findViewById(R.id.settings_arrow_back)
        val themeSwitcher: SwitchMaterial = findViewById(R.id.themeSwitcher)
        val shareButton: LinearLayout = findViewById(R.id.share_button)
        val supportButton: LinearLayout = findViewById(R.id.support_button)
        val privacyAgreementButton: LinearLayout = findViewById(R.id.privacy_agreement_button)

        // Установка начального состояния переключателя темы
        themeSwitcher.isChecked = viewModel.themeState.value ?: false

        // Обработка нажатия на кнопку "Назад"
        buttonSettingsArrowBack.setOnClickListener {
            finish()
        }

        // Обработка изменения темы
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }

        // Подписка на изменения темы
        viewModel.themeState.observe(this) { isDarkThemeEnabled ->
            applyTheme(isDarkThemeEnabled)
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

    private fun applyTheme(isDarkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
