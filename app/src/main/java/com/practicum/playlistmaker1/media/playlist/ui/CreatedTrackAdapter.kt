package com.practicum.playlistmaker1.media.playlist.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.practicum.playlistmaker1.search.domain.models.Track
import com.practicum.playlistmaker1.search.ui.TrackViewHolder

class CreatedTrackAdapter (
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
) : ListAdapter<Track, CreatedTrackViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreatedTrackViewHolder {
        return CreatedTrackViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CreatedTrackViewHolder, position: Int) {
        holder.bind(getItem(position), onTrackClick, onTrackLongClick)
    }

    fun updateData(newTracks: List<Track>) {
        submitList(newTracks.toList())
    }

    class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.trackId == newItem.trackId
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem == newItem
        }
    }
}