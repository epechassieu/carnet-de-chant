package fr.epechassieu.carnetdechant.data.mapper

import fr.epechassieu.carnetdechant.data.database.entities.SongEntity
import fr.epechassieu.carnetdechant.data.remote.SongDto
import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song

// Entity -> Domain
fun SongEntity.toDomain(): Song {
    return Song(
        id = id,
        songbook = songbook,
        number = number,
        title = title,
        categories = if (categories.isBlank()) {
            emptyList()
        } else {
            categories.split(",").mapNotNull { categoryName ->
                try {
                    Category.valueOf(categoryName)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        },
        lyrics = lyrics,
        urlMedia = urlMedia
    )
}

// Domain -> Entity
fun Song.toEntity(): SongEntity {
    return SongEntity(
        id = id,
        songbook = songbook,
        number = number,
        title = title,
        categories = categories.joinToString(",") { it.name },
        lyrics = lyrics,
        urlMedia = urlMedia
    )
}

// DTO -> Entity
fun SongDto.toEntity(): SongEntity {
    return SongEntity(
        id = id,
        songbook = recueil,
        number = numero,
        title = titre,
        categories = categories.joinToString(","),
        lyrics = paroles,
        urlMedia = urlmedia
    )
}
