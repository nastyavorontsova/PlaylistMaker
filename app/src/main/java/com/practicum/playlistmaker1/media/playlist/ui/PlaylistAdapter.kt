package com.practicum.playlistmaker1.media.playlist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker1.R
import com.practicum.playlistmaker1.databinding.ItemPlaylistBinding
import com.practicum.playlistmaker1.media.playlist.data.db.dao.Playlist
import java.io.File

class PlaylistAdapter(
    private val onItemClick: (Playlist) -> Unit
) : ListAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder>(PlaylistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PlaylistViewHolder(
        private val binding: ItemPlaylistBinding,
        private val onItemClick: (Playlist) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            with(binding) {
                val radius = root.context.resources.getDimensionPixelSize(R.dimen.cover_art_radius)

                if (playlist.coverPath != null) {
                    Glide.with(root.context)
                        .load(File(playlist.coverPath))
                        .transform(CenterCrop(),RoundedCorners(radius))
                        .into(coverPath)
                } else {
                    coverPath.setImageResource(R.drawable.placeholder_cover)
                }

                name.text = playlist.name
                tracksCount.text = "${playlist.tracksCount} треков"

                root.setOnClickListener { onItemClick(playlist) }
            }
        }

    }

    class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist) =
            oldItem == newItem
    }
}