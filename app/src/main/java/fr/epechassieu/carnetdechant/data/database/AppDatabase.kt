package fr.epechassieu.carnetdechant.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.epechassieu.carnetdechant.data.database.dao.SongDao
import fr.epechassieu.carnetdechant.data.database.dao.UrlMediaUserDao
import fr.epechassieu.carnetdechant.data.database.entities.SongEntity
import fr.epechassieu.carnetdechant.data.database.entities.UrlMediaUserEntity

@Database(
    entities = [SongEntity::class, UrlMediaUserEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao

    abstract fun urlMediaUserDao(): UrlMediaUserDao
}