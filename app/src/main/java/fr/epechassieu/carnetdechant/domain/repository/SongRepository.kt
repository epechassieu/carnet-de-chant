package fr.epechassieu.carnetdechant.domain.repository

import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun getAllSongs(): Flow<List<Song>>

    fun getSongsByTitle(): Flow<List<Song>>

    fun getSongsById(): Flow<List<Song>>

    fun getSongsByCategory(category: Category): Flow<List<Song>>

    fun getSongsById(id: String): Flow<Song?>

    fun searchSongs(query: String): Flow<List<Song>>

    suspend fun isDatabaseEmpty(): Boolean

    suspend fun loadSongsFromJson()

    suspend fun getCategoriesWithCount(): Map<Category, Int>
}