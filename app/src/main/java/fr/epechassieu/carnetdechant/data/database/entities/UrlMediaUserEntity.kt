package fr.epechassieu.carnetdechant.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


/**
 * Represents a user-defined media URL associated with a specific song in the database.
 *
 * This entity allows users to link external resources (like YouTube videos, audio files, or web pages)
 * to a song. It maintains a many-to-one relationship with the [SongEntity].
 *
 * @property id The unique identifier for this media entry (auto-generated).
 * @property songId The identifier of the song this URL is associated with.
 * @property url The actual URL string pointing to the media resource.
 */
@Entity(
    tableName = "url_media_user",
    foreignKeys = [
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["song_id"]
        )
    ],
    indices = [Index(value = ["song_id"])]
)
data class UrlMediaUserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "song_id")
    val songId: String,

    @ColumnInfo(name = "url")
    val url: String
)
