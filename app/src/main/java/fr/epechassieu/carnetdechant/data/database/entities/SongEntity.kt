package fr.epechassieu.carnetdechant.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id:String,

    @ColumnInfo(name = "recueil")
    val songbook: String,

    @ColumnInfo(name = "numero")
    val number: Int,

    @ColumnInfo(name = "titre")
    val title: String,

    @ColumnInfo(name = "categories")
    val categories: String,

    @ColumnInfo(name = "paroles")
    val lyrics: String,

    @ColumnInfo(name = "url_media")
    val urlMedia: String?
)
