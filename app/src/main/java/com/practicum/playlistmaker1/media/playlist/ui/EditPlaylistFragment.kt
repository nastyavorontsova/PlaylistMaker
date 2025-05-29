package com.practicum.playlistmaker1.media.playlist.ui

import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.practicum.playlistmaker1.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

import android.net.Uri
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.practicum.playlistmaker1.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker1.media.playlist.data.db.dao.Playlist

class EditPlaylistFragment : PlaylistFragment() {

    companion object {
        private const val ARG_PLAYLIST_ID = "playlist_id"

        fun newInstance(playlistId: Long): EditPlaylistFragment {
            return EditPlaylistFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PLAYLIST_ID, playlistId)
                }
            }
        }
    }

    private val args: EditPlaylistFragmentArgs by navArgs()

    override val viewModel: EditPlaylistViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val playlistId = args.playlistId
        viewModel.loadPlaylistById(playlistId)

        if (playlistId != -1L) {
            viewModel.loadPlaylistById(playlistId)
        } else {
            // Обработка ошибки - id не передан или неверен
            Toast.makeText(context, "Ошибка загрузки плейлиста", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = getString(R.string.edit_playlist)
        binding.createButton.text = getString(R.string.save)

        // Подписываемся на LiveData с данными плейлиста
        viewModel.playlistData.observe(viewLifecycleOwner) { playlist ->
            playlist?.let {
                selectedCoverUri = it.coverPath?.let { path -> Uri.parse(path) }
                if (selectedCoverUri != null) {
                    binding.svgImageView.setImageURI(selectedCoverUri)
                } else {
                    loadDefaultCover()
                }

                binding.inputEditTextName.setText(it.name)
                binding.inputEditTextDescription.setText(it.description ?: "")

                updateCreateButtonState(it.name.isNotBlank())
            }
        }

        binding.createButton.setOnClickListener {
            val name = binding.inputEditTextName.text.toString()
            val description = binding.inputEditTextDescription.text?.toString()
            if (name.isNotBlank()) {
                viewModel.updatePlaylist(name, description, selectedCoverUri)
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack() // Просто закрываем без сохранения
        }

        viewModel.playlistUpdated.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Плейлист сохранён", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack() // Закрываем без сохранения
            }
        })
    }
}
