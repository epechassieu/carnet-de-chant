package fr.epechassieu.carnetdechant.ui.songdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.epechassieu.carnetdechant.domain.usecases.GetSongByIdUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class SongDetailViewModel @Inject constructor(
    private val getSongByIdUseCase: GetSongByIdUseCase,
    savedStateHandle: SavedStateHandle // C'est magique : ça récupère les arguments de navigation
) : ViewModel() {

    // On récupère l'ID du chant passé dans l'URL de navigation
    private val songId: String = checkNotNull(savedStateHandle["songId"])

    // On transforme le Flow<Song?> en Flow<SongDetailUiState>
    val uiState: StateFlow<SongDetailUiState> = getSongByIdUseCase(songId)
        .map { song ->
            if (song != null) SongDetailUiState.Success(song) else SongDetailUiState.Error
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SongDetailUiState.Loading
        )
}