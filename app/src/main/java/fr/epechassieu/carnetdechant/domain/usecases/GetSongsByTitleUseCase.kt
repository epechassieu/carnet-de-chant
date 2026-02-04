package fr.epechassieu.carnetdechant.domain.usecases

import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**`
 *   ` * Use case to retrieve all songs ordered by their title.`
 *   ` *`
 *   ` * @property songRepository The repository providing access to song data.`
 */
class GetSongsByTitleUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    operator fun invoke(): Flow<List<Song>> {
        return songRepository.getSongsByTitle()
    }
}