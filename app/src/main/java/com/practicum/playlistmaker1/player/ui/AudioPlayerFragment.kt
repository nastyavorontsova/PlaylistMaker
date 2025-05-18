package com.practicum.playlistmaker1.player.ui

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker1.R
import com.practicum.playlistmaker1.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker1.media.playlist.ui.PlaylistBottomSheetAdapter
import com.practicum.playlistmaker1.search.domain.models.Track

import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AudioPlayerViewModel by viewModel()

    private lateinit var track: Track
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var adapter: PlaylistBottomSheetAdapter
    private lateinit var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getParcelable<Track>("TRACK_DATA")?.let { receivedTrack ->
            this.track = receivedTrack
            viewModel.initialize(track)
        } ?: run {
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!this::track.isInitialized) {
            findNavController().popBackStack()
            return
        }

        setupUI()
        setupObservers()
        setupButtons()
        setupBottomSheet()
        setupPlaylistsRecyclerView()
    }

    private fun setupUI() {
        updateUI()

        val cornerRadius = resources.getDimensionPixelSize(R.dimen.cover_art_radius)

        Glide.with(requireContext())
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder_cover)
            .transform(RoundedCorners(cornerRadius))
            .into(binding.coverArt)
    }

    private fun updateUI() {
        binding.songName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.albumName.text = track.collectionName ?: getString(R.string.unknown_album)
        binding.releaseDate.text = track.releaseDate
        binding.genre.text = track.primaryGenreName
        binding.country.text = track.country
        binding.trackDuration.text = formatTrackDuration(track.trackTimeMillis)
    }

    private fun setupObservers() {
        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            binding.playPauseButton.setImageResource(
                when (state) {
                    AudioPlayerViewModel.PlayerState.PLAYING -> R.drawable.ic_pause
                    else -> R.drawable.ic_play
                }
            )
        }

        viewModel.progress.observe(viewLifecycleOwner) { progress ->
            binding.progress.text = progress
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            binding.addToFavoritesButton.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite
            )
        }
    }

    private fun setupButtons() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.playPauseButton.setOnClickListener {
            viewModel.playPause()
        }

        binding.addToFavoritesButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }
    }

    private fun setupBottomSheet() {
        val displayMetrics = DisplayMetrics().apply {
            requireActivity().windowManager.defaultDisplay.getMetrics(this)
        }
        val screenHeight = displayMetrics.heightPixels

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheet).apply {
            peekHeight = (screenHeight * 0.66).toInt()
            state = BottomSheetBehavior.STATE_HIDDEN
            isFitToContents = false
            halfExpandedRatio = 0.66f
            skipCollapsed = false
        }

        bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                _binding?.overlay?.visibility = when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> View.GONE
                    else -> View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                _binding?.overlay?.alpha = slideOffset.coerceAtLeast(0f)
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        binding.addToPlaylistButton.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                viewModel.loadPlaylists()
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        binding.newPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_global_playlistFragment)
        }
    }

    private fun setupPlaylistsRecyclerView() {
        adapter = PlaylistBottomSheetAdapter { playlist ->
            viewModel.addTrackToPlaylist(playlist)
        }

        binding.playlistsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistsRecyclerView.adapter = adapter

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            adapter.submitList(playlists)
            binding.playlistsRecyclerView.visibility = if (playlists.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.addToPlaylistStatus.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { status ->
                when (status) {
                    is AudioPlayerViewModel.AddToPlaylistStatus.Success -> {
                        Toast.makeText(
                            requireContext(),
                            "Добавлено в плейлист ${status.playlistName}",
                            Toast.LENGTH_SHORT
                        ).show()
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }

                    is AudioPlayerViewModel.AddToPlaylistStatus.AlreadyExists -> {
                        Toast.makeText(
                            requireContext(),
                            "Трек уже добавлен в плейлист ${status.playlistName}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun formatTrackDuration(duration: Long): String {
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.playerState.value == AudioPlayerViewModel.PlayerState.PLAYING) {
            viewModel.playPause()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback)
        viewModel.releaseMediaPlayer()
        _binding = null
    }

    companion object {
        fun newInstance(track: Track): AudioPlayerFragment {
            return AudioPlayerFragment().apply {
                arguments = bundleOf("TRACK_DATA" to track)
            }
        }
    }
}
