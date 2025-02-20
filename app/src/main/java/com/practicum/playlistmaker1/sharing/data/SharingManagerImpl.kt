package com.practicum.playlistmaker1.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker1.sharing.domain.SharingManager
import com.practicum.playlistmaker1.R

class SharingManagerImpl(private val context: Context) : SharingManager {

    override fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_app_text))
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareIntent, null))
    }

    override fun openSupport() {
        val email = context.getString(R.string.support_email)
        val subject = context.getString(R.string.support_subject)
        val body = context.getString(R.string.support_body)

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        context.startActivity(Intent.createChooser(emailIntent, null))
    }

    override fun openPrivacyAgreement() {
        val url = context.getString(R.string.user_agreement_url)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }
}