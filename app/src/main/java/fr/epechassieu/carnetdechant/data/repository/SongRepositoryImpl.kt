package fr.epechassieu.carnetdechant.data.repository

import android.content.Context
import android.database.SQLException
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.epechassieu.carnetdechant.R
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.JsonConvertException
import fr.epechassieu.carnetdechant.data.database.dao.SongDao
import fr.epechassieu.carnetdechant.data.mapper.toDomain
import fr.epechassieu.carnetdechant.data.mapper.toEntity
import fr.epechassieu.carnetdechant.data.remote.SongApiService
import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject


/**
 * Implementation of [SongRepository] that manages song data synchronization between
 * a local Room database and a remote API service.
 *
 * This repository handles the data flow by fetching song entities from the local [SongDao]
 * and converting them to domain [Song] models. It also provides functionality to
 * initialize the local database using data retrieved from [SongApiService].
 *
 * @property songDao The Data Access Object for local song storage operations.
 * @property songApiService The service used to fetch song data from a remote source.
 */
class SongRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songDao: SongDao,
    private val songApiService: SongApiService
) : SongRepository {

    override fun getSongsByTitle(): Flow<List<Song>> {
        return songDao.getSongsByTitle().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSongsByCategory(category: Category): Flow<List<Song>> {
        return songDao.getSongsByCategory(category.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSongById(id: String): Flow<Song?> {
        return songDao.getSongById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun isDatabaseEmpty(): Boolean {
        return songDao.getSongsCount() == 0
    }

    /**
     * Synchronizes the local database by fetching songs from the remote API.
     *
     * This function performs the following steps:
     * 1. Downloads song data from the remote service.
     * 2. Maps the response data to local database entities.
     * 3. Persists the entities into the local Room database.
     *
     * It handles various error scenarios including network connectivity issues,
     * API errors, and database transaction failures, returning a localized
     * error message wrapped in a [Result].
     *
     * @return A [Result] containing a success message with the number of imported songs,
     * or a failure containing an exception with a user-friendly error message.
     */
    override suspend fun loadSongsFromJson(): Result<String> {
        return try {
            //download
            val response = songApiService.getSongs()
            //transform
            if (response.chants.isEmpty()) {
                throw IOException("Le serveur a renvoyé une liste vide.")
            }
            val entities = response.chants.map { it.toEntity() }
            //memory phone
            songDao.insertAll(entities)
            Result.success("Succès : ${entities.size} chants importés.")

        } catch (e: Exception) {
            val userMessage = when (e) {
                // no internet
                is UnknownHostException -> context.getString(R.string.error_network)

                //github ok - Json not ok
                is JsonConvertException -> context.getString(R.string.error_file_not_found)

                // Json not formed
                is SerializationException -> context.getString(R.string.error_file_corrupt)

                // error http 404, 500
                is ClientRequestException -> context.getString(R.string.error_http_client, e.response.status.value)
                is ServerResponseException -> context.getString(R.string.error_server_unavailable, e.response.status.value)

                // database error
                is SQLException -> context.getString(R.string.error_database)

                // unknown error
                else -> context.getString(R.string.error_unknown, e.message ?: "")
            }
            e.printStackTrace()
            Result.failure(Exception(userMessage))
        }
    }

}
