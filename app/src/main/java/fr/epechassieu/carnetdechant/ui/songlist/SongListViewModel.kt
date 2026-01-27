package fr.epechassieu.carnetdechant.ui.songlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.epechassieu.carnetdechant.domain.usecases.GetAllSongsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SongListViewModel @Inject constructor(
    private val getAllSongsUseCase: GetAllSongsUseCase
) : ViewModel() {

    // 1. Gestion de la Recherche
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // 2. L'État UI combiné (Liste + Recherche)
    val uiState: StateFlow<SongListUiState> = combine(
        getAllSongsUseCase(),
        _searchQuery
    ) { songs, query ->
        if (query.isBlank()) {
            SongListUiState.Success(songs)
        } else {
            val filtered = songs.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.lyrics.contains(query, ignoreCase = true)
            }
            SongListUiState.Success(filtered)
        }
                // LE FIX IMPORTANT POUR KSP :
                // On force le type de retour du bloc combine
                as SongListUiState
    }
        .catch { error ->
            emit(SongListUiState.Error(error.message ?: "Erreur inconnue"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SongListUiState.Loading
        )

    // Action pour modifier la recherche
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}