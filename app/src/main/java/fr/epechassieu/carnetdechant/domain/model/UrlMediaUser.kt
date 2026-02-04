package fr.epechassieu.carnetdechant.domain.model

/**
 * Represents a media URL (e.g., YouTube link, external audio) associated with a specific song
 * by a user.
 *
 * @property id Unique identifier for the media entry.
 * @property songId The unique identifier of the song this media belongs to.
 * @property url The actual web link or path to the media resource.
 */
data class UrlMediaUser(
    val id: Long = 0,
    val songId: String,
    val url: String
)
