package fr.epechassieu.carnetdechant.ui.songdetail

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.epechassieu.carnetdechant.R
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.repository.AudioPlayerRepository
import fr.epechassieu.carnetdechant.domain.usecases.GetSongByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
    private val audioRepository: AudioPlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SongDetailUiState>(SongDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val playerState: StateFlow<PlayerState> = combine(
        audioRepository.isPlaying,
        audioRepository.currentPosition,
        audioRepository.duration,
        audioRepository.buffering,
        audioRepository.error
    ) { isPlaying, position, duration, buffering, error ->
        PlayerState(
            isPrepared = duration > 0,
            isPlaying = isPlaying,
            currentPosition = position,
            duration = duration,
            isBuffering = buffering,
            error = error,
            songTitle = (_uiState.value as? SongDetailUiState.Success)?.song?.title ?: ""
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerState())

    fun loadSong(songId: String) {
        viewModelScope.launch {
            getSongByIdUseCase(songId)
                .catch { _uiState.value = SongDetailUiState.Error(it.message ?: "Erreur inconnue") }
                .collect { song ->
                    if (song != null) {
                        _uiState.value = SongDetailUiState.Success(song)
                    } else {
                        _uiState.value = SongDetailUiState.Error("Chant introuvable")

                    }
                }
        }
    }

        fun prepareAudio(song: Song) {
            song.audioUrl?.let { url ->
                audioRepository.prepare(url)
            }
        }

        fun togglePlayPause() {
            if (audioRepository.isPlaying.value) {
                audioRepository.pause()
            } else {
                audioRepository.play()
            }
        }

        fun seekTo(position: Long) {
            audioRepository.seekTo(position)
        }

        fun stopAudio() {
            audioRepository.stop()
        }

        override fun onCleared() {
            super.onCleared()
            audioRepository.release()
        }
    }
