package fr.epechassieu.carnetdechant.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Represents a song record in the local database.
 *
 * @property id The unique identifier for the song.
 * @property songbook The name of the songbook or collection (e.g., "recueil").
 * @property number The index or number of the song within the songbook.
 * @property title The title of the song.
 * @property categories A string representation of the categories associated with the song.
 * @property lyrics The full text content of the song.
 * @property urlMedia An optional URL pointing to an external media resource (audio/video).
 */
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
