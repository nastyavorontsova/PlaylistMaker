package com.practicum.playlistmaker1.media.data.db

import com.practicum.playlistmaker1.media.data.db.entity.FavouriteTrackEntity
import com.practicum.playlistmaker1.search.data.dto.TrackDto
import com.practicum.playlistmaker1.search.domain.models.Track

class TrackDbConvertor {

    // Преобразование из DTO (сетевой модели) в Entity (для БД)
    fun fromDtoToEntity(dto: TrackDto): FavouriteTrackEntity {
        return FavouriteTrackEntity(
            trackId = dto.trackId,
            trackName = dto.trackName,
            artistName = dto.artistName,
            collectionName = dto.collectionName,
            releaseDate = dto.releaseDate,
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            trackTimeMillis = dto.trackTimeMillis,
            previewUrl = dto.previewUrl,
            artworkUrl100 = dto.artworkUrl100
        )
    }

    // Преобразование из Entity в Domain модель (БД)
    fun fromEntityToDomain(entity: FavouriteTrackEntity): Track {
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            trackTimeMillis = entity.trackTimeMillis,
            previewUrl = entity.previewUrl,
            artworkUrl100 = entity.artworkUrl100,
            isFavorite = true // Трек из БД всегда избранный
        )
    }

    // Преобразование из Domain модели в Entity (для сохранения в БД)
    fun fromDomainToEntity(track: Track): FavouriteTrackEntity {
        return FavouriteTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            previewUrl = track.previewUrl,
            artworkUrl100 = track.artworkUrl100
        )
    }

    // Дополнительный метод: преобразование DTO в Domain с учетом избранного статуса
    fun fromDtoToDomain(dto: TrackDto, isFavorite: Boolean = false): Track {
        return Track(
            trackId = dto.trackId,
            trackName = dto.trackName,
            artistName = dto.artistName,
            collectionName = dto.collectionName,
            releaseDate = dto.releaseDate,
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            trackTimeMillis = dto.trackTimeMillis,
            previewUrl = dto.previewUrl,
            artworkUrl100 = dto.artworkUrl100,
            isFavorite = isFavorite
        )
    }

    fun fromDomainToDto(track: Track): TrackDto {
        return TrackDto(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            previewUrl = track.previewUrl,
            artworkUrl100 = track.artworkUrl100
        )
    }
}
