package com.practicum.playlistmaker1.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker1.databinding.FragmentPlaylistsBinding
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistsViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Кнопка для создания нового плейлиста (пока без обработки)
        binding.newPlaylistButton.setOnClickListener {
            // Обработка нажатия будет добавлена в след спринтах
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}