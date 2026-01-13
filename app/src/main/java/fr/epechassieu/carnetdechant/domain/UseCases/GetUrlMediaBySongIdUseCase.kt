package fr.epechassieu.carnetdechant.domain.UseCases

import fr.epechassieu.carnetdechant.domain.model.UrlMedia
import fr.epechassieu.carnetdechant.domain.repository.UrlMediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUrlMediaBySongIdUseCase @Inject constructor(
    private val urlMediaRepository: UrlMediaRepository
) {
    operator fun invoke(chantId: String): Flow<List<UrlMedia>> {
        return urlMediaRepository.getUrlMediaBySongId(chantId)
    }
}