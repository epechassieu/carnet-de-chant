package fr.epechassieu.carnetdechant.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.epechassieu.carnetdechant.domain.model.TextSize
import fr.epechassieu.carnetdechant.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val textSize: StateFlow<TextSize> = settingsRepository.textSize
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TextSize.Default
        )

    fun setTextSize(textSize: TextSize) {
        viewModelScope.launch {
            settingsRepository.setTextSize(textSize)
        }
    }
}
