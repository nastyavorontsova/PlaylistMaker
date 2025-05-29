package com.practicum.playlistmaker1.media.playlist.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker1.R
import com.practicum.playlistmaker1.databinding.FragmentCreatedPlaylistBinding
import com.practicum.playlistmaker1.databinding.ItemBsPlaylistBinding
import com.practicum.playlistmaker1.media.playlist.data.db.dao.Playlist
import com.practicum.playlistmaker1.search.domain.models.Track
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatedPlaylistFragment : Fragment() {

    private var _binding: FragmentCreatedPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreatedPlaylistViewModel by viewModel()
    private var playlistId: Long = -1L
    private lateinit var adapter: CreatedTrackAdapter

    private lateinit var playlistBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuPlaylistBinding: ItemBsPlaylistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            playlistId = it.getLong("playlistId", -1L)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatedPlaylistBinding.inflate(inflater, container, false)

        // Инициализация биндинга для элемента меню
        menuPlaylistBinding = ItemBsPlaylistBinding.inflate(inflater, binding.menuBottomSheet, false)

        // Настройка поведения overlay
        binding.overlay.setOnClickListener {
            hideMenuBottomSheet()
        }

        // Настройка поведения Bottom Sheet меню
        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.visibility = View.GONE
                            binding.menuBottomSheet.visibility = View.GONE
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    val currentBinding = _binding ?: return
                    if (slideOffset <= 0f) {
                        currentBinding.overlay.alpha = 1f
                    } else {
                        currentBinding.overlay.alpha = slideOffset
                    }
                }
            })
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheet()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        if (playlistId != -1L) {
            viewModel.loadPlaylist(playlistId)
        } else {
            findNavController().popBackStack()
        }
    }

    private fun setupBottomSheet() {
        binding.shareAndMore.post {
            val location = IntArray(2)
            binding.shareAndMore.getLocationOnScreen(location)
            val yPosition = location[1] + binding.shareAndMore.height

            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenHeight = displayMetrics.heightPixels

            playlistBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheet).apply {
                peekHeight = screenHeight - yPosition - convertDpToPx(16)
                isHideable = false
                state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun convertDpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun setupRecyclerView() {
        adapter = CreatedTrackAdapter(
            onTrackClick = { track ->
                findNavController().navigate(
                    R.id.action_global_audioPlayerFragment,
                    bundleOf("TRACK_DATA" to track)
                )
            },
            onTrackLongClick = { track ->
                showDeleteTrackDialog(track)
            }
        )

        binding.playlistsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CreatedPlaylistFragment.adapter
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_track_title))
            .setMessage(getString(R.string.delete_track_message))
            .setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .setPositiveButton(getString(R.string.delete)) { dialogInterface, _ ->
                viewModel.removeTrack(track.trackId)
                dialogInterface.dismiss()
            }
            .show()

        // Установка цветов кнопок после отображения
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.blue)
        )
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.red)
        )
    }

    private fun setupObservers() {
        viewModel.playlistData.observe(viewLifecycleOwner) { data ->
            data?.let { (playlist, tracks) ->
                bindPlaylistData(playlist, tracks)
            } ?: run {
                findNavController().popBackStack()
            }
        }

        viewModel.showShareDialog.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { shareText ->
                sharePlaylist(shareText)
            }
        }

        viewModel.showEmptyPlaylistToast.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.empty_playlist_share_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.navigateToMediaLibrary.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_global_mediaLibraryFragment)
            }
        }
    }

    private fun sharePlaylist(shareText: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun bindPlaylistData(playlist: Playlist, tracks: List<Track>) {
        with(binding) {
            if (playlist.coverPath != null) {
                Glide.with(requireContext())
                    .load(File(playlist.coverPath))
                    .placeholder(R.drawable.placeholder_cover)
                    .into(coverPath)
            } else {
                coverPath.setImageResource(R.drawable.placeholder_cover)
            }

            name.text = playlist.name
            description.text = playlist.description ?: ""

            tracksCount.text = resources.getQuantityString(
                R.plurals.tracks_count,
                playlist.tracksCount,
                playlist.tracksCount
            )

            val durationSum = tracks.sumOf { it.trackTimeMillis }
            duration.text = SimpleDateFormat("mm", Locale.getDefault())
                .format(durationSum) + " " + getString(R.string.minutes)

            if (tracks.isNotEmpty()) {
                adapter.submitList(tracks)
                playlistsRecyclerView.visibility = View.VISIBLE
                emptyTracks.visibility = View.GONE
            } else {
                playlistsRecyclerView.visibility = View.GONE
                emptyTracks.visibility = View.VISIBLE
            }

            bindMenuPlaylistInfo(playlist)
        }
    }

    private fun bindMenuPlaylistInfo(playlist: Playlist) {
        with(binding) {
            val radius = resources.getDimensionPixelSize(R.dimen.button_radius)

            if (playlist.coverPath != null) {
                Glide.with(requireContext())
                    .load(File(playlist.coverPath))
                    .transform(CenterCrop(), RoundedCorners(radius))
                    .into(coverPathPlaylist)
            } else {
                Glide.with(requireContext())
                    .load(R.drawable.placeholder_cover)
                    .transform(CenterCrop(), RoundedCorners(radius))
                    .into(coverPathPlaylist)
            }

            namePlaylist.text = playlist.name
            tracksCountPlaylist.text = "${playlist.tracksCount} треков"
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.share.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.more.setOnClickListener {
            if (viewModel.playlistData.value != null) {
                showMenuBottomSheet()
            }
        }

        binding.aboutPlaylistShare.setOnClickListener {
            viewModel.sharePlaylist()
            hideMenuBottomSheet()
        }

        binding.deletePlaylist.setOnClickListener {
            showDeletePlaylistDialog()
            hideMenuBottomSheet()
        }

        binding.overlay.setOnClickListener {
            hideMenuBottomSheet()
        }

        binding.editInfo.setOnClickListener {
            viewModel.playlistData.value?.let { (playlist, _) ->
                // Используем safe args или проверяем на null
                val args = bundleOf("playlistId" to playlist.id)
                try {
                    findNavController().navigate(
                        R.id.action_createdPlaylistFragment_to_editPlaylistFragment,
                        args
                    )
                    hideMenuBottomSheet()
                } catch (e: Exception) {
                    Log.e("Navigation", "Failed to navigate to edit fragment", e)
                }
            }
        }
    }

    private fun showMenuBottomSheet() {
        binding.overlay.apply {
            visibility = View.VISIBLE
            alpha = 1f // Принудительно установить видимость
        }
        binding.menuBottomSheet.visibility = View.VISIBLE

        // Принудительно свернуть и снова развернуть, чтобы был триггер смены состояния
        if (menuBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.menuBottomSheet.postDelayed({
                // Установка состояния вручную
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                // На случай, если onSlide не вызовется — принудительно поддержим overlay
                binding.overlay.alpha = 1f
            }, 50)
        } else {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.overlay.alpha = 1f // дублируем на случай отсутствия slideOffset
        }
    }

    private fun hideMenuBottomSheet() {
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun showDeletePlaylistDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_playlist_title))
            .setMessage(getString(R.string.delete_playlist_message))
            .setNegativeButton(getString(R.string.no)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { dialogInterface, _ ->
                viewModel.deletePlaylist()
                dialogInterface.dismiss()
            }
            .show()

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.blue)
        )
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.red)
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(playlistId: Long): CreatedPlaylistFragment {
            return CreatedPlaylistFragment().apply {
                arguments = bundleOf("playlistId" to playlistId)
            }
        }
    }
}
