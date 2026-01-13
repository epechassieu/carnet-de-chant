package fr.epechassieu.carnetdechant.domain.UseCases

import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import javax.inject.Inject

class isDatabaseEmptyUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(): Boolean {
        return songRepository.isDatabaseEmpty()
    }
}