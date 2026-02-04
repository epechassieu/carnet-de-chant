package fr.epechassieu.carnetdechant.ui.songfilter

import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song

/**
 * Represents the UI state for the song filtering screen.
 *
 * @property categories The complete list of available song categories.
 * @property selectedCategory The currently selected category for filtering. `null` if no category is selected.
 * @property filteredSongs The list of songs that match the current filter criteria (i.e., belong to the [selectedCategory]).
 */
data class SongFilterUiState (
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val filteredSongs: List<Song> = emptyList()
)