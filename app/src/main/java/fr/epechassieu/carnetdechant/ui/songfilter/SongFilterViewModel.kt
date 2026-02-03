package fr.epechassieu.carnetdechant.ui.songfilter

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.usecases.GetSongsByCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@OptIn (ExperimentalMaterial3Api::class)
@HiltViewModel
class SongFilterViewModel @Inject constructor(
    private val getSongsByCategoryUseCase: GetSongsByCategoryUseCase
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<Category?>(null)

    val uiState: StateFlow<SongFilterUiState> = _selectedCategory
        .flatMapLatest { category ->
        if (category == null) {
            flowOf(SongFilterUiState(selectedCategory = null, filteredSongs = emptyList()))
        } else {
            getSongsByCategoryUseCase(category).map({ songs ->
                SongFilterUiState(selectedCategory = category, filteredSongs = songs)
            })
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SongFilterUiState()
    )
            fun selectCategory(category: Category) {
                _selectedCategory.value = category
            }

            fun clearSelection() {
                _selectedCategory.value = null
            }
}