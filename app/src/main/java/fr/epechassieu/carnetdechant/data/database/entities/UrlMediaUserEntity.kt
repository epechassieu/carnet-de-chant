package fr.epechassieu.carnetdechant.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


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
