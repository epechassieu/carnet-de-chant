package fr.epechassieu.carnetdechant.domain.usecases

import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser
import fr.epechassieu.carnetdechant.domain.repository.UrlMediaUserRepository
import javax.inject.Inject

/**
 * Use case responsible for adding a new [UrlMediaUser] to the data repository.
 *
 * @property urlMediaUserRepository The repository interface used to persist the media URL data.
 */
class AddUrlMediaUserUseCase @Inject constructor(
    private val urlMediaUserRepository: UrlMediaUserRepository
) {
    suspend operator fun invoke(urlMediaUser: UrlMediaUser) {
        urlMediaUserRepository.addUrlMediaUser(urlMediaUser)
    }
}