package fr.epechassieu.carnetdechant.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.epechassieu.carnetdechant.data.database.dao.SongDao
import fr.epechassieu.carnetdechant.data.database.entities.SongEntity

/**
 * Main Room database configuration for the application.
 *
 * This database provides access to the persistent storage for song data and user-defined media URLs.
 * It manages the [SongEntity] and [UrlMediaUserEntity] tables and uses [Converters] for complex data types.
 */
@Database(
    entities = [SongEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao

}