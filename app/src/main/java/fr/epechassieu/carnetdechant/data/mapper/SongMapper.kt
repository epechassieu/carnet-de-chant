package fr.epechassieu.carnetdechant.data.mapper

import fr.epechassieu.carnetdechant.data.database.entities.SongEntity
import fr.epechassieu.carnetdechant.data.remote.SongDto
import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song

/**
 * Maps a [SongEntity] from the local database to a [Song] domain model.
 *
 * This conversion includes parsing the comma-separated categories string into a list of
 * [Category] enum constants, ignoring any values that do not match the existing enum.
 *
 * @return A [Song] instance containing the data from the entity.
 */
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

/**
 * Converts a [Song] domain model to a [SongEntity] for database storage.
 *
 * @return A [SongEntity] containing the song's data, with categories serialized as a comma-separated string.
 */
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

/**
 * Maps a [Song] domain model to a [SongEntity] for database storage.
 *
 * @return A [SongEntity] containing the song's data, with categories serialized as a comma-separated string.
 */
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
