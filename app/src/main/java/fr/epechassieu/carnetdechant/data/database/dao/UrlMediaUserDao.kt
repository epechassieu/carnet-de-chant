package fr.epechassieu.carnetdechant.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import fr.epechassieu.carnetdechant.data.database.entities.UrlMediaUserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UrlMediaUserDao {

    @Query("SELECT * FROM url_media_user WHERE song_id = :songId")
    fun getUrlMediaUserBySongId(songId: String): Flow<List<UrlMediaUserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(urlMediaUser: UrlMediaUserEntity)

    @Delete
    suspend fun delete(urlMediaUser: UrlMediaUserEntity)
}