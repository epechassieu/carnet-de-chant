package fr.epechassieu.carnetdechant.ui.songlist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.epechassieu.carnetdechant.R
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.usecases.GetSongsByTitleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel responsible for managing the state and business logic of the song list screen.
 *
 * This ViewModel handles the retrieval of songs via [getSongsByTitleUseCase] and provides
 * a search functionality to filter songs by title or lyrics. It exposes the current
 * UI state through a [StateFlow] of [SongListUiState].
 *
 * @property getSongsByTitleUseCase The use case used to retrieve the stream of songs.
 */
@HiltViewModel
class SongListViewModel @Inject constructor(
    private val getSongsByTitleUseCase: GetSongsByTitleUseCase,
    @param:ApplicationContext private val context: Context

) : ViewModel() {

    // 1. search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // 2. uiState combine (List + search)
    val uiState: StateFlow<SongListUiState> = combine<List<Song>, String,
            SongListUiState>(
        getSongsByTitleUseCase(),
        _searchQuery.debounce(200)
    ) { songs, query ->
        SongListUiState.Success(filterSongs(songs, query))
    }
        .catch { error ->
            emit(SongListUiState.Error(context.getString(R.string.error_database)))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SongListUiState.Loading
        )

    // modif search
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun filterSongs(songs: List<Song>, query: String): List<Song> {
        if (query.isBlank()) return songs
        return songs.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.lyrics.contains(query, ignoreCase = true) ||
                    it.number.toString().contains(query)
        }
    }
}