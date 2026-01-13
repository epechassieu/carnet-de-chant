package fr.epechassieu.carnetdechant.domain.UseCases

import fr.epechassieu.carnetdechant.domain.model.UrlMedia
import fr.epechassieu.carnetdechant.domain.repository.UrlMediaRepository
import javax.inject.Inject

class AddUrlMediaUseCase @Inject constructor(
    private val urlMediaRepository: UrlMediaRepository
) {
    suspend operator fun invoke(urlMedia: UrlMedia) {
        urlMediaRepository.addUrlMedia(urlMedia)
    }
}