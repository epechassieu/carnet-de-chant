package fr.epechassieu.carnetdechant.domain.usecases

import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser
import fr.epechassieu.carnetdechant.domain.repository.UrlMediaUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUrlMediaUserBySongIdUseCase @Inject constructor(
    private val urlMediaUserRepository: UrlMediaUserRepository
) {
    operator fun invoke(chantId: String): Flow<List<UrlMediaUser>> {
        return urlMediaUserRepository.getUrlMediaUserBySongId(chantId)
    }
}