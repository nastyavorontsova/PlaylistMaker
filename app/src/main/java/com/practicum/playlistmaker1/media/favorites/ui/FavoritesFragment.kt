package com.practicum.playlistmaker1.media.favorites.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker1.R
import com.practicum.playlistmaker1.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker1.player.ui.AudioPlayerFragment
import com.practicum.playlistmaker1.search.domain.models.Track
import com.practicum.playlistmaker1.search.ui.SearchFragmentDirections
import com.practicum.playlistmaker1.search.ui.TrackAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModel()

    private lateinit var adapter: TrackAdapter
    private var isClickAllowed = true
    private var debounceJob: Job? = null

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance() = FavoritesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initAdapter()
        observeViewModel()
    }

    private fun initViews() {
        binding.trackFavoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initAdapter() {
        adapter = TrackAdapter { track ->
            if (clickDebounce()) {
                navigateToPlayer(track)
            }
        }
        binding.trackFavoritesRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                FavoritesViewModel.State.Empty -> showEmptyState()
                is FavoritesViewModel.State.Content -> showContent(state.tracks)
            }
        }
    }

    private fun showContent(tracks: List<Track>) {
        adapter.updateData(tracks)
        binding.trackFavoritesRecyclerView.visibility = View.VISIBLE
        binding.emptyState.visibility = View.GONE
    }

    private fun showEmptyState() {
        binding.trackFavoritesRecyclerView.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
    }


    private fun navigateToPlayer(track: Track) {
        val bundle = bundleOf("TRACK_DATA" to track.copy())
        findNavController().navigate(R.id.action_mediaLibraryFragment_to_audioPlayerFragment, bundle)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            debounceJob?.cancel()
            debounceJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    override fun onDestroyView() {
        super.onDestroyView()
        debounceJob?.cancel()
        _binding = null
    }
}