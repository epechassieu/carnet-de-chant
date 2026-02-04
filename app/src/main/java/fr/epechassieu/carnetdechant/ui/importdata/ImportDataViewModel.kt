package fr.epechassieu.carnetdechant.ui.importdata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import fr.epechassieu.carnetdechant.ui.importdata.ImportDataUiState
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
    private val songRepository: SongRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ImportDataUiState>(ImportDataUiState.Idle)
    val uiState: StateFlow<ImportDataUiState> = _uiState.asStateFlow()

    fun importSongs() {
        viewModelScope.launch {
            _uiState.value = ImportDataUiState.Loading
            // 1. On appelle le Repository.
            // ATTENTION : Ça ne "plante" plus ici, ça renvoie un résultat (Succès ou Échec)
            val result = songRepository.loadSongsFromJson()

            // 2. On regarde ce qu'il y a dans la boîte
            result.onSuccess { message ->
                // C'est gagné !
                // Tu pourrais même utiliser 'message' ("X chants importés") si tu voulais l'afficher
                _uiState.value = ImportDataUiState.Success
            }.onFailure { error ->
                // C'est perdu !
                // 'error' contient l'exception avec ton message traduit ("Pas de connexion", etc.)
                _uiState.value = ImportDataUiState.Error(error.message ?: "Erreur inconnue")
            }
        }
    }
}