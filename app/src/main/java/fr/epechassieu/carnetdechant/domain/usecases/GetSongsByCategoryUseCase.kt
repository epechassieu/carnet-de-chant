package fr.epechassieu.carnetdechant.domain.usecases

import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSongsByCategoryUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    operator fun invoke(category: Category): Flow<List<Song>> {
        return songRepository.getSongsByCategory(category)
    }
}