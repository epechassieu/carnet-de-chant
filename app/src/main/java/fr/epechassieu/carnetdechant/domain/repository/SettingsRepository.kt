package fr.epechassieu.carnetdechant.domain.repository

import fr.epechassieu.carnetdechant.domain.model.TextSize
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing user-configurable app settings.
 */
interface SettingsRepository {

    val textSize: Flow<TextSize>

    suspend fun setTextSize(textSize: TextSize)
}
