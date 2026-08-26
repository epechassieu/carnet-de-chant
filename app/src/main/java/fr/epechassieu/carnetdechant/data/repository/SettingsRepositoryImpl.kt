package fr.epechassieu.carnetdechant.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import fr.epechassieu.carnetdechant.domain.model.TextSize
import fr.epechassieu.carnetdechant.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private val textSizeKey = stringPreferencesKey("text_size")

    override val textSize = dataStore.data.map { preferences ->
        preferences[textSizeKey]?.let { name ->
            runCatching { TextSize.valueOf(name) }.getOrNull()
        } ?: TextSize.Default
    }

    override suspend fun setTextSize(textSize: TextSize) {
        dataStore.edit { preferences ->
            preferences[textSizeKey] = textSize.name
        }
    }
}
