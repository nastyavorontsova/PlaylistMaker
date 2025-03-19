package com.practicum.playlistmaker1.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker1.R
import com.practicum.playlistmaker1.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker1.sharing.domain.EventType
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Установка начального состояния переключателя темы
        binding.themeSwitcher.isChecked = viewModel.themeState.value ?: false

        // Обработка изменения темы
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }

        // Подписка на изменения темы
        viewModel.themeState.observe(viewLifecycleOwner) { isDarkThemeEnabled ->
            applyTheme(isDarkThemeEnabled)
        }

        // Обработка нажатия на кнопку "Поделиться"
        binding.shareButton.setOnClickListener {
            viewModel.shareApp()
        }

        // Обработка нажатия на кнопку "Написать в поддержку"
        binding.supportButton.setOnClickListener {
            viewModel.openSupport()
        }

        // Обработка нажатия на кнопку "Пользовательское соглашение"
        binding.privacyAgreementButton.setOnClickListener {
            viewModel.openPrivacyAgreement()
        }

        // Подписка на события
        viewModel.event.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { eventType ->
                when (eventType) {
                    EventType.ShareApp -> shareApp()
                    EventType.OpenSupport -> openSupport()
                    EventType.OpenPrivacyAgreement -> openPrivacyAgreement()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
}