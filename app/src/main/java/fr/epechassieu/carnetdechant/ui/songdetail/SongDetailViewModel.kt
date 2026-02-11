package fr.epechassieu.carnetdechant.ui.songdetail

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.epechassieu.carnetdechant.R
import fr.epechassieu.carnetdechant.domain.usecases.GetSongByIdUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


/**
 * ViewModel responsible for managing the state of the song detail screen.
 *
 * It retrieves the song ID from the [SavedStateHandle], fetches the corresponding
 * song data using [GetSongByIdUseCase], and exposes the result as a [StateFlow]
 * of [SongDetailUiState].
 *
 * @property getSongByIdUseCase Use case to retrieve a song's information by its unique identifier.
 * @param savedStateHandle Handle to saved state, used here to extract the "songId" navigation argument.
 */
@HiltViewModel
class SongDetailViewModel @Inject constructor(
    private val getSongByIdUseCase: GetSongByIdUseCase,
    savedStateHandle: SavedStateHandle,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    // retrieve Id from navigation argument
    private val songId: String = checkNotNull(savedStateHandle["songId"])

    // transform Flow<Song?> into Flow<SongDetailUiState>
    val uiState: StateFlow<SongDetailUiState> = getSongByIdUseCase(songId)

        .map { song ->
            if (song != null) {
                SongDetailUiState.Success(song)
            } else SongDetailUiState.Error(context.getString(R.string.error_song_not_found))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SongDetailUiState.Loading
        )
}