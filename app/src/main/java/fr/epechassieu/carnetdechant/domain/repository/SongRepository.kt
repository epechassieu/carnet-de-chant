package fr.epechassieu.carnetdechant.domain.repository

import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing and accessing [Song] data.
 *
 * This interface provides a contract for retrieving songs filtered by various criteria
 * and handling the initial population of the song database.
 */
interface SongRepository {

    fun getSongsByTitle(): Flow<List<Song>>

    fun getSongsByCategory(category: Category): Flow<List<Song>>

    fun getSongById(id: String): Flow<Song?>

    suspend fun isDatabaseEmpty(): Boolean

    suspend fun loadSongsFromJson() : Result<String>

}