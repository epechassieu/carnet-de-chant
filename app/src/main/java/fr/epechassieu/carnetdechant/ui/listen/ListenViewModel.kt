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

    private fun loadData() {
        viewModelScope.launch {
            // Charger les infos du chant (titre + lien officiel)
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