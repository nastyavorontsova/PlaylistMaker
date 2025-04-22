package com.practicum.playlistmaker1.media.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker1.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker1.player.ui.AudioPlayerActivity
import com.practicum.playlistmaker1.search.domain.models.Track
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
        val intent = Intent(requireActivity(), AudioPlayerActivity::class.java).apply {
             putExtra("TRACK_DATA", track)
         }
         startActivity(intent)
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