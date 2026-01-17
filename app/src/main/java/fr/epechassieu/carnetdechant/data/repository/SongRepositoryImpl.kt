package fr.epechassieu.carnetdechant.data.repository

import fr.epechassieu.carnetdechant.data.database.dao.SongDao
import fr.epechassieu.carnetdechant.data.mapper.toDomain
import fr.epechassieu.carnetdechant.data.mapper.toEntity
import fr.epechassieu.carnetdechant.data.remote.api.SongApiService
import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val songDao: SongDao,
    private val songApiService: SongApiService
) : SongRepository {

    override fun getAllSongs(): Flow<List<Song>> {
        return songDao.getAllSongs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSongsByTitle(): Flow<List<Song>> {
        return songDao.getSongsByTitle().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSongsById(): Flow<List<Song>> {
        return songDao.getSongsById().map { entities ->
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

    override fun searchSongs(query: String): Flow<List<Song>> {
        return songDao.searchSongs(query).map { entities ->
            entities.map { it.toDomain() }
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