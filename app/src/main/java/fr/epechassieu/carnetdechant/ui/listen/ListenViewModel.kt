package fr.epechassieu.carnetdechant.ui.listen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser
import fr.epechassieu.carnetdechant.domain.repository.UrlMediaUserRepository
import fr.epechassieu.carnetdechant.domain.usecases.GetSongByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    private val urlMediaUserRepository: UrlMediaUserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val songId: String = checkNotNull(savedStateHandle["songId"])

    private val _uiState = MutableStateFlow(ListenUiState())
    val uiState: StateFlow<ListenUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    /**
     * Loads the song details and associated user media URLs.
     *
     * This function launches two concurrent coroutines to:
     * 1. Fetch the song's basic information (title and official media URL) via [getSongByIdUseCase].
     * 2. Fetch any custom media URLs provided by the user via [urlMediaUserRepository].
     *
     * Updates the [_uiState] with the retrieved data or an error message if the song is not found.
     */
    private fun loadData() {
        viewModelScope.launch {
            // Charger les infos du chant
            getSongByIdUseCase(songId).collect { song ->
                if (song != null) {
                    _uiState.update {
                        it.copy(
                            songTitle = song.title,
                            officialUrl = song.urlMedia,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(error = "Chant introuvable", isLoading = false)
                    }
                }
            }
        }

        // Charger les liens utilisateur
        viewModelScope.launch {
            urlMediaUserRepository.getUrlMediaUserBySongId(songId).collect { urls ->
                _uiState.update { it.copy(userUrls = urls) }
            }
        }
    }

    fun addUrl(url: String) {
        if (url.isBlank()) return
        viewModelScope.launch {
            val newUrl = UrlMediaUser(songId = songId, url = url)
            urlMediaUserRepository.addUrlMediaUser(newUrl)
        }
    }

    fun deleteUrl(urlMediaUser: UrlMediaUser) {
        viewModelScope.launch {
            urlMediaUserRepository.deleteUrlMediaUser(urlMediaUser)
        }
    }
}