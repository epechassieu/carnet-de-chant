package fr.epechassieu.carnetdechant.domain.model

/**
 * Represents a song within a songbook.
 *
 * @property id Unique identifier for the song.
 * @property songbook The name or identifier of the songbook this song belongs to.
 * @property number The specific number/index of the song within the songbook.
 * @property title The title of the song.
 * @property categories A list of categories or themes associated with the song.
 * @property lyrics The full text content of the song.
 * @property urlMedia An optional URL pointing to a media resource (audio or video) for the song.
 */
data class Song(
    val id: String,
    val songbook: String,
    val number: Int,
    val title: String,
    val categories: List<Category>,
    val lyrics: String,
    val audio: String? = null,
    val audioUrl: String? = null
)
