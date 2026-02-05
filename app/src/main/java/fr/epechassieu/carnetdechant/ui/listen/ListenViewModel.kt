package fr.epechassieu.carnetdechant.ui.listen

import fr.epechassieu.carnetdechant.R
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser
import fr.epechassieu.carnetdechant.domain.repository.UrlMediaUserRepository
import fr.epechassieu.carnetdechant.domain.usecases.GetSongByIdUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.epechassieu.carnetdechant.domain.exception.AppException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing the state of the "Listen" screen.
 *
 * It retrieves and provides the official media URL for a specific song as well as
 * custom media URLs added by the user. It handles the loading of song details
 * and allows for adding or deleting user-specific media links.
 *
 * @property getSongByIdUseCase Use case to fetch song details including the official media link.
 * @property urlMediaUserRepository Repository to manage user-defined media URLs for the song.
 * @property savedStateHandle Handle to access the "songId" passed as a navigation argument.
 */
@HiltViewModel
class ListenViewModel @Inject constructor(
    getSongByIdUseCase: GetSongByIdUseCase,
    private val urlMediaUserRepository: UrlMediaUserRepository,
    savedStateHandle: SavedStateHandle,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val songId: String = checkNotNull(savedStateHandle["songId"])

    //---local state for error handling ---
    private val _actionError = MutableStateFlow<String?>(null)

    // song id and url media

    val uiState: StateFlow<ListenUiState> = combine(
        getSongByIdUseCase(songId),
        urlMediaUserRepository.getUrlMediaUserBySongId(songId),
        _actionError
    ) { song, userUrls, actionError ->
        if (song == null) {
            ListenUiState(error = context.getString(R.string.error_song_not_found), isLoading = false)
        } else {
            ListenUiState(
                songTitle = song.title,
                officialUrl = song.urlMedia,
                userUrls = userUrls,
                error = actionError,
                isLoading = false
            )
        }
    }
        .catch { e ->
            emit(ListenUiState(error = mapErrorToMessage(e)))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ListenUiState(isLoading = true)
        )

    fun addUrl(url: String) {
        if (url.isBlank()) return

        viewModelScope.launch {
            _actionError.value = null

            val newUrl = UrlMediaUser(songId = songId, url = url)
            urlMediaUserRepository.addUrlMediaUser(newUrl)
                .onFailure { error ->
                    // declanche le combine
                    _actionError.value = mapErrorToMessage(error)
                }
        }
    }

    fun deleteUrl(urlMediaUser: UrlMediaUser) {
        viewModelScope.launch {
            _actionError.value = null

            urlMediaUserRepository.deleteUrlMediaUser(urlMediaUser)
                .onFailure { error ->
                    // declanche le combine
                    _actionError.value = mapErrorToMessage(error)
                }
        }
    }

    private fun mapErrorToMessage(error: Throwable): String {
        return when (error) {
            is AppException.DatabaseError -> context.getString(R.string.error_database)
            is AppException.Unknown -> context.getString(
                R.string.error_unknown,
                error.message ?: ""
            )

            else -> context.getString(R.string.error_unknown, error.message ?: "")
        }
    }

    fun clearError() {
        _actionError.value = null
    }
}
