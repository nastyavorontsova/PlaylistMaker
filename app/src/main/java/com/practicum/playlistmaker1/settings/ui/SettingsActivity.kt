package com.practicum.playlistmaker1.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker1.R
import com.practicum.playlistmaker1.sharing.domain.Event
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

        viewModel.event.observe(this) { event ->
            when (event) {
                is Event.ShareApp -> shareApp()
                is Event.OpenSupport -> openSupport()
                is Event.OpenPrivacyAgreement -> openPrivacyAgreement()
            }
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

    private fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun openSupport() {
        val email = getString(R.string.support_email)
        val subject = getString(R.string.support_subject)
        val body = getString(R.string.support_body)

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        startActivity(Intent.createChooser(emailIntent, null))
    }

    private fun openPrivacyAgreement() {
        val url = getString(R.string.user_agreement_url)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
}
