package fr.epechassieu.carnetdechant.domain.usecases

import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import javax.inject.Inject

/**
 * Use case responsible for triggering the initial loading or synchronization of songs from a JSON source
 * into the application's repository.
 *
 * @property songRepository The repository interface used to handle song data operations.
 */
class LoadSongsFromJsonUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke() : Result<Int> {
        return songRepository.loadSongsFromJson()
    }
}