package fr.epechassieu.carnetdechant.domain.UseCases

import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSongsByNumberUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    operator fun invoke(): Flow<List<Song>> {
        return songRepository.getSongsByNumber()
    }
}