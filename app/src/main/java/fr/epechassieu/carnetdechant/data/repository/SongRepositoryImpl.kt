package fr.epechassieu.carnetdechant.data.repository

import fr.epechassieu.carnetdechant.data.database.dao.SongDao
import fr.epechassieu.carnetdechant.data.mapper.toDomain
import fr.epechassieu.carnetdechant.data.mapper.toEntity
import fr.epechassieu.carnetdechant.data.remote.SongApiService
import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override suspend fun loadSongsFromJson() {
        val response = songApiService.getSongs()
        val entities = response.chants.map { it.toEntity() }
        songDao.insertAll(entities)
    }

}