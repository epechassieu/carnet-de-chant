package fr.epechassieu.carnetdechant.data.remote

import kotlinx.serialization.Serializable

/**
 * Data transfer object representing the response containing a collection of songs and metadata.
 *
 * @property version The version of the song database or API response.
 * @property dateGeneration The timestamp or date when the data was generated.
 * @property nombreChants The total number of songs included in this response.
 * @property chants The list of [SongDto] objects containing the detailed information for each song.
 */
@Serializable
data class SongsResponseDto(
    val version: String,
    val dateGeneration: String,
    val nombreChants: Int,
    val chants: List<SongDto>
)

/**
 * Data transfer object representing a song's details as retrieved from the remote API.
 *
 * @property id The unique identifier for the song.
 * @property recueil The name of the songbook or collection the song belongs to.
 * @property numero The index number of the song within its songbook.
 * @property titre The title of the song.
 * @property categories A list of labels or themes associated with the song.
 * @property paroles The full lyrics of the song.
 * @property urlmedia An optional URL pointing to an audio or video resource for the song.
 */
@Serializable
data class SongDto(
    val id: String,
    val recueil: String,
    val numero: Int,
    val titre: String,
    val categories: List<String>,
    val paroles: String,
    val urlmedia: String? = null
)