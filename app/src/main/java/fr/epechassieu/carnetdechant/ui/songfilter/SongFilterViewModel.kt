package fr.epechassieu.carnetdechant.ui.songfilter

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.epechassieu.carnetdechant.R
import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.usecases.GetSongsByCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class SongFilterViewModel @Inject constructor(
    private val getSongsByCategoryUseCase: GetSongsByCategoryUseCase,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    // Liste des catégories à afficher (sans INCONNU)
    private val displayedCategories = Category.entries.filter { it != Category.INCONNU }


    val uiState: StateFlow<SongFilterUiState> = _selectedCategory
        .flatMapLatest { category ->
            if (category == null) {
                // use for clickback to return at initial grid
                flowOf(SongFilterUiState(categories = displayedCategories))
            } else {
                getSongsByCategoryUseCase(category).map { songs ->
                    SongFilterUiState(
                        categories = displayedCategories,
                        selectedCategory = category,
                        filteredSongs = songs)
                }
            }
        }
        .catch { e ->
            emit(SongFilterUiState(
                categories = displayedCategories,
                error = context.getString(R.string.error_database)
            ))
        }

        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SongFilterUiState(categories = displayedCategories)
        )

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun clearSelection() {
        _selectedCategory.value = null
    }
}