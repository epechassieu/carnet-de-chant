package fr.epechassieu.carnetdechant.data.repository

import fr.epechassieu.carnetdechant.data.database.dao.UrlMediaUserDao
import fr.epechassieu.carnetdechant.data.mapper.toDomain
import fr.epechassieu.carnetdechant.data.mapper.toEntity
import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser
import fr.epechassieu.carnetdechant.domain.repository.UrlMediaUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [UrlMediaUserRepository] that manages user-provided media URLs.
 *
 * This repository acts as a bridge between the data layer ([UrlMediaUserDao]) and the domain layer,
 * handling the mapping between database entities and domain models.
 *
 * @property urlMediaUserDao The Data Access Object used to perform CRUD operations on media URL entities.
 */
class UrlMediaUserRepositoryImpl @Inject constructor(
    private val urlMediaUserDao: UrlMediaUserDao
) : UrlMediaUserRepository {

    override fun getUrlMediaUserBySongId(songId: String): Flow<List<UrlMediaUser>> {
        return urlMediaUserDao.getUrlMediaUserBySongId(songId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addUrlMediaUser(urlMediaUser: UrlMediaUser) {
        urlMediaUserDao.insert(urlMediaUser.toEntity())
    }

    override suspend fun deleteUrlMediaUser(urlMediaUser: UrlMediaUser) {
        urlMediaUserDao.delete(urlMediaUser.toEntity())
    }
}