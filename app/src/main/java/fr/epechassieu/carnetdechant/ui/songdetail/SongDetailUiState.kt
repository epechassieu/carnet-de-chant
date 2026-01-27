package fr.epechassieu.carnetdechant.ui.songdetail

import fr.epechassieu.carnetdechant.domain.model.Song

sealed interface SongDetailUiState {
    data object Loading : SongDetailUiState
    data class Success(val song: Song) : SongDetailUiState
    data object Error : SongDetailUiState
}