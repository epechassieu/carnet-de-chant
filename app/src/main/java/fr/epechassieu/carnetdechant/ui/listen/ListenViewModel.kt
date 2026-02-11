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
import fr.epechassieu.carnetdechant.domain.usecases.AddUrlMediaUserUseCase
import fr.epechassieu.carnetdechant.domain.usecases.DeleteUrlMediaUserUseCase
import fr.epechassieu.carnetdechant.domain.usecases.GetUrlMediaUserBySongIdUseCase
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
    private val getSongByIdUseCase: GetSongByIdUseCase,
    private val getUrlMediaUserBySongIdUseCase: GetUrlMediaUserBySongIdUseCase,
    private val addUrlMediaUserUseCase: AddUrlMediaUserUseCase,
    private val deleteUrlMediaUserUseCase: DeleteUrlMediaUserUseCase,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val songId: String = checkNotNull(savedStateHandle["songId"])
    private val _actionError = MutableStateFlow<String?>(null)
    private val _newUrlText = MutableStateFlow("")

    // song id and url media

    val uiState: StateFlow<ListenUiState> = combine(
        getSongByIdUseCase(songId),
        getUrlMediaUserBySongIdUseCase(songId),
        _actionError,
        _newUrlText
    ) { song, userUrls, actionError, newUrlText ->
        if (song == null) {
            ListenUiState(
                error = context.getString(R.string.error_song_not_found),
                isLoading = false
            )
        } else {
            ListenUiState(
                songTitle = song.title,
                officialUrl = song.urlMedia,
                userUrls = userUrls,
                error = actionError,
                isLoading = false,
                newUrlText = newUrlText
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

    fun onNewUrlTextChange(text: String) {
        _newUrlText.value = text
    }

    fun addUrl() {
        val url = _newUrlText.value
        if (url.isBlank()) return

        viewModelScope.launch {
            _actionError.value = null

            val newUrl = UrlMediaUser(songId = songId, url = url)
            addUrlMediaUserUseCase(newUrl)
                .onSuccess {
                    _newUrlText.value = ""
                }
                .onFailure { error ->
                    _actionError.value = mapErrorToMessage(error)
                }
        }
    }

    fun deleteUrl(urlMediaUser: UrlMediaUser) {
        viewModelScope.launch {
            _actionError.value = null

            deleteUrlMediaUserUseCase(urlMediaUser)
                .onFailure { error ->
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
