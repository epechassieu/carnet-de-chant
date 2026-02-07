package fr.epechassieu.carnetdechant.domain.usecases

import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser
import fr.epechassieu.carnetdechant.domain.repository.UrlMediaUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**`
 *   ` * Use case to retrieve all url media user
 *   ` *`
 *   ` * @property urlMediaUserRepository The repository providing access to url data.`
 */
class GetUrlMediaUserBySongIdUseCase @Inject constructor(
    private val urlMediaUserRepository: UrlMediaUserRepository
) {
    operator fun invoke(songId: String): Flow<List<UrlMediaUser>> {
        return urlMediaUserRepository.getUrlMediaUserBySongId(songId)
    }
}