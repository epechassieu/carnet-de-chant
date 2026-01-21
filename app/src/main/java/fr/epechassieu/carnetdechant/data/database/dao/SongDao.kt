package fr.epechassieu.carnetdechant.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.epechassieu.carnetdechant.data.database.entities.SongEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    fun getAllSongs(): Flow<List<SongEntity>>

    // ascending ranking

    @Query("SELECT * FROM songs ORDER BY titre ASC")
    fun getSongsByTitle(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs ORDER BY numero ASC")
    fun getSongsByNumber(): Flow<List<SongEntity>>

    // search by criterias

    @Query("SELECT * FROM songs WHERE titre LIKE '%' || :query || '%'")
    fun searchSongs(query: String): Flow<List<SongEntity>>

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

    @Query("SELECT categories FROM songs")
    suspend fun getAllCategoriesRaw(): List<String>

}