package fr.epechassieu.carnetdechant.ui.listen

import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser

/**
 * Represents the UI state for the listening screen.
 *
 * @property officialUrl The URL of the official media source, if available.
 * @property userUrls A list of media URLs provided by users.
 * @property songTitle The title of the song being played or viewed.
 * @property isLoading Indicates whether the media data is currently being loaded.
 * @property error An optional error message to be displayed if something goes wrong.
 */
data class ListenUiState(
    val officialUrl: String? = null,
    val userUrls: List<UrlMediaUser> = emptyList(),
    val songTitle: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)