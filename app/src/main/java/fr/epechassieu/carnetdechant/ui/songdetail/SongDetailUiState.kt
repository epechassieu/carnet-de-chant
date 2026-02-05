package fr.epechassieu.carnetdechant.ui.songdetail

import fr.epechassieu.carnetdechant.domain.model.Song

/**
 * Represents the various UI states for the Song Detail screen.
 *
 * This sealed interface defines the possible states the UI can be in while fetching
 * and displaying a specific song's information.
 */
sealed interface SongDetailUiState {
    data object Loading : SongDetailUiState
    data class Success(val song: Song) : SongDetailUiState
    data class Error (val message: String): SongDetailUiState
}