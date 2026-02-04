package fr.epechassieu.carnetdechant.domain.usecases

import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get a song by its ID.
 * @property songRepository The repository used to get the song.
 */
class GetSongByIdUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    operator fun invoke(id:String): Flow<Song?> {
        return songRepository.getSongById(id)
    }
}