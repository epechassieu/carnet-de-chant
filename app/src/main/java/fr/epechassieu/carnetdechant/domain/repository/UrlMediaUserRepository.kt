package fr.epechassieu.carnetdechant.domain.repository

import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser
import kotlinx.coroutines.flow.Flow

interface UrlMediaUserRepository {

    fun getUrlMediaUserBySongId(songId: String): Flow<List<UrlMediaUser>>

    suspend fun addUrlMediaUser(urlMediaUser: UrlMediaUser)

    suspend fun deleteUrlMediaUser(urlMediaUser: UrlMediaUser)

}