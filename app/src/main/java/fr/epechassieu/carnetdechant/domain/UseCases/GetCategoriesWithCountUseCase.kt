package fr.epechassieu.carnetdechant.domain.UseCases

import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import javax.inject.Inject

class GetCategoriesWithCountUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(): Map<Category, Int> {
        return songRepository.getCategoriesWithCount()
    }
}