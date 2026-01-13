package fr.epechassieu.carnetdechant.domain.repository

import fr.epechassieu.carnetdechant.domain.model.UrlMedia
import kotlinx.coroutines.flow.Flow

interface UrlMediaRepository {

    fun getUrlMediaBySongId(chantId: String): Flow<List<UrlMedia>>

    suspend fun addUrlMedia(urlMedia: UrlMedia)

    suspend fun deleteUrlMedia(urlMedia: UrlMedia)

    suspend fun updateUrlMedia(urlMedia: UrlMedia)
}