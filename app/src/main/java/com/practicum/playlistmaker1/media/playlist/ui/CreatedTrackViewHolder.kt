package com.practicum.playlistmaker1.media.playlist.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker1.R
import com.practicum.playlistmaker1.search.domain.models.Track
import com.practicum.playlistmaker1.search.ui.TrackViewHolder

class CreatedTrackViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackNameTextView: TextView = itemView.findViewById(R.id.trackNameTextView)
    private val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
    private val trackTimeTextView: TextView = itemView.findViewById(R.id.trackTimeTextView)
    private val artworkImageView: ImageView = itemView.findViewById(R.id.artworkImageView)

    fun bind(track: Track, onClick: (Track) -> Unit, onLongClick: (Track) -> Unit) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackTimeTextView.text = track.getFormattedDuration()

        val cornerRadius = itemView.resources.getDimensionPixelSize(R.dimen.button_radius)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder_cover)
            .error(R.drawable.placeholder_cover)
            .fallback(R.drawable.placeholder_cover)
            .fitCenter()
            .transform(RoundedCorners(cornerRadius))
            .into(artworkImageView)

        itemView.setOnClickListener { onClick(track) }
        itemView.setOnLongClickListener {
            onLongClick(track)
            true
        }
    }

    companion object {
        fun create(parent: ViewGroup): CreatedTrackViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_track, parent, false)
            return CreatedTrackViewHolder(view)
        }
    }
}