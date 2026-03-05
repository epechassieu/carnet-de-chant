package fr.epechassieu.carnetdechant.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import fr.epechassieu.carnetdechant.data.database.entities.SongEntity
import kotlinx.coroutines.flow.Flow


/**
 * Data Access Object (DAO) for managing [SongEntity] instances in the local database.
 * Provides methods for querying, inserting, and counting songs.
 */
@Dao
interface SongDao {

    // ascending ranking

    @Query("SELECT * FROM songs ORDER BY titre ASC")
    fun getSongsByTitle(): Flow<List<SongEntity>>

    // search by criterias

    @Query("SELECT * FROM songs WHERE categories LIKE '%' || :category || '%'")
    fun getSongsByCategory(category: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id = :id")
    fun getSongById(id: String): Flow<SongEntity?>

    // Json Update

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<SongEntity>)

    // count

    @Query("SELECT COUNT(*) FROM songs")
    suspend fun getSongsCount(): Int

    // deleting
    @Query("DELETE FROM songs")
    suspend fun deleteAll()

    // atomic update
    @Transaction
    suspend fun replaceAllSongs(songs: List<SongEntity>) {
        deleteAll()
        insertAll(songs)
    }
}