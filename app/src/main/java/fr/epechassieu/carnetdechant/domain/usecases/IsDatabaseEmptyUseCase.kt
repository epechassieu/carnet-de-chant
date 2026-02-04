package fr.epechassieu.carnetdechant.domain.usecases

import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import javax.inject.Inject

/**
 * Use case responsible for checking whether the song database is currently empty.
 *
 * This is typically used during application startup to determine if initial data
 * needs to be synchronized or if a welcome screen should be displayed.
 *
 * @property songRepository The repository used to access song data.
 */
class IsDatabaseEmptyUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(): Boolean {
        return songRepository.isDatabaseEmpty()
    }
}