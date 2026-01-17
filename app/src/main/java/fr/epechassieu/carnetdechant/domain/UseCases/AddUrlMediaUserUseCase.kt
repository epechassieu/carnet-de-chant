package fr.epechassieu.carnetdechant.domain.UseCases

import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser
import fr.epechassieu.carnetdechant.domain.repository.UrlMediaUserRepository
import javax.inject.Inject

class AddUrlMediaUserUseCase @Inject constructor(
    private val urlMediaUserRepository: UrlMediaUserRepository
) {
    suspend operator fun invoke(urlMediaUser: UrlMediaUser) {
        urlMediaUserRepository.addUrlMedia(urlMediaUser)
    }
}