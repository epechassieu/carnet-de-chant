package fr.epechassieu.carnetdechant.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey
    val id:String,
    val songbook: String,
    val number: Int,
    val title: String,
    val categories: String,
    val lyrics: String,
    val urlMedia: String?
)
