package fr.epechassieu.carnetdechant.ui.songfilter

import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song

data class SongFilterUiState (
    val selectedCategory: Category? = null,
    val filteredSongs: List<Song> = emptyList()
)