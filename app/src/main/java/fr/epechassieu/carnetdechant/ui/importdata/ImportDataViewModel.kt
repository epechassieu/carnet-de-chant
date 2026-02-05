package fr.epechassieu.carnetdechant.ui.importdata

import fr.epechassieu.carnetdechant.R
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.epechassieu.carnetdechant.domain.exception.AppException
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing the logic of importing song data.
 *
 * It coordinates the process of loading songs from a data source (JSON) via the [SongRepository]
 * and exposes the current state of the operation through [uiState].
 *
 * @property songRepository The repository used to perform the song data import.
 */
@HiltViewModel
class ImportDataViewModel @Inject constructor(
    private val songRepository: SongRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<ImportDataUiState>(ImportDataUiState.Idle)
    val uiState: StateFlow<ImportDataUiState> = _uiState.asStateFlow()

    fun importSongs() {
        viewModelScope.launch {
            _uiState.value = ImportDataUiState.Loading

            songRepository.loadSongsFromJson()
                .onSuccess { count ->
                    _uiState.value = ImportDataUiState.Success(
                        context.getString(R.string.import_success, count)
                    )
                }
                .onFailure { error ->
                    val message = when (error) {
                        is AppException.NetworkError -> context.getString(R.string.error_network)
                        is AppException.FileNotFound -> context.getString(R.string.error_file_not_found)
                        is AppException.FileCorrupt -> context.getString(R.string.error_file_corrupt)
                        is AppException.HttpClientError -> context.getString(R.string.error_http_client, error.code)
                        is AppException.ServerError -> context.getString(R.string.error_server_unavailable, error.code)
                        is AppException.DatabaseError -> context.getString(R.string.error_database)
                        is AppException.Unknown -> context.getString(R.string.error_unknown, error.message ?: "")
                        else -> context.getString(R.string.error_unknown, "")
                    }
                    _uiState.value = ImportDataUiState.Error(message)
                }
        }
    }
}