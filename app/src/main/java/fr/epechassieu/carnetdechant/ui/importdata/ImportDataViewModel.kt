package fr.epechassieu.carnetdechant.ui.importdata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportDataViewModel @Inject constructor(
    private val songRepository: SongRepository
) : ViewModel() {

    fun importSongs() {
        viewModelScope.launch {
            try {
                songRepository.loadSongsFromJson()
                // Ici, tu pourrais ajouter un log pour dire "Import lancé"
            } catch (e: Exception) {
                android.util.Log.e("IMPORTDEBUG","Erreur d'import : ${e.message}",e)
                e.printStackTrace()
            }
        }
    }
}