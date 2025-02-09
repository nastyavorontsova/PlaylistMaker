package com.practicum.playlistmaker1.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker1.presentation.App
import com.practicum.playlistmaker1.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonSettingsArrowBack: ImageView = findViewById(R.id.settings_arrow_back)

        buttonSettingsArrowBack.setOnClickListener {
            finish()
        }

        val themeSwitcher: SwitchMaterial = findViewById(R.id.themeSwitcher)
        val shareButton: LinearLayout = findViewById(R.id.share_button)
        val supportButton: LinearLayout = findViewById(R.id.support_button)
        val privacyAgreementButton: LinearLayout = findViewById(R.id.privacy_agreement_button)

        themeSwitcher.isChecked = (applicationContext as App).darkTheme

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        shareButton.setOnClickListener {
            shareApp()
        }

        supportButton.setOnClickListener {
            writeToSupport()
        }

        privacyAgreementButton.setOnClickListener {
            openPrivacyAgreement()
        }
    }

    private fun shareApp() {

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun writeToSupport() {
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
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}
