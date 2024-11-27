package com.practicum.playlistmaker1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var trackNameTextView: TextView = itemView.findViewById(R.id.trackNameTextView)
    private var artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
    private var trackTimeTextView: TextView = itemView.findViewById(R.id.trackTimeTextView)
    private var artworkImageView: ImageView = itemView.findViewById(R.id.artworkImageView)

    fun bind(track: Track) {
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
    }

    companion object {
        fun create(parent: ViewGroup): TrackViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
            return TrackViewHolder(view)
        }
    }
}