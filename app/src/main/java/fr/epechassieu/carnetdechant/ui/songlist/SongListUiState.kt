package fr.epechassieu.carnetdechant.ui.songlist

import fr.epechassieu.carnetdechant.domain.model.Song

/**
 * Represents the different states of the UI for the song list screen.
 *
 * This sealed interface defines the possible states the UI can be in:
 * - [Loading]: The song list is currently being fetched.
 * - [Success]: The song list has been successfully loaded and contains a list of [Song] objects.
 * - [Error]: An error occurred while loading the song list, containing an error message.
 */
sealed interface SongListUiState {
    data object Loading : SongListUiState
    data class Success(val songs: List<Song>) : SongListUiState
    data class Error(val message: String) : SongListUiState
}