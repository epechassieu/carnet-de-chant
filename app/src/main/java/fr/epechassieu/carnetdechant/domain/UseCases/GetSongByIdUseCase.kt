package fr.epechassieu.carnetdechant.domain.UseCases

import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSongByIdUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    operator fun invoke(id:String): Flow<Song?> {
        return songRepository.getSongById(id)
    }
}