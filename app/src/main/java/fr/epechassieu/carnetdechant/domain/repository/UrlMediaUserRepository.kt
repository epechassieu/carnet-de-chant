package fr.epechassieu.carnetdechant.domain.repository

import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing [UrlMediaUser] entities.
 * Provides methods to retrieve, add, and delete user-defined media URLs associated with songs.
 */
interface UrlMediaUserRepository {

    fun getUrlMediaUserBySongId(songId: String): Flow<List<UrlMediaUser>>

    suspend fun addUrlMediaUser(urlMediaUser: UrlMediaUser) : Result<Unit>

    suspend fun deleteUrlMediaUser(urlMediaUser: UrlMediaUser) : Result<Unit>
}
