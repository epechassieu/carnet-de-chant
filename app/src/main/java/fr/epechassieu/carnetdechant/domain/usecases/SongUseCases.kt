package fr.epechassieu.carnetdechant.domain.usecases

import javax.inject.Inject

/**
 * A wrapper class that bundles all song-related use cases.
 *
 * This class serves as a central point of access for domain layer operations
 * concerning songs, such as retrieving, searching, or managing song data.
 */
data class SongUseCases @Inject constructor(
    //val getAllSongs: GetAllSongsUseCase,
    val getSongById: GetSongByIdUseCase,
    val getSongsByCategory: GetSongsByCategoryUseCase,
    val getSongsByTitle: GetSongsByTitleUseCase,
    val loadSongsFromJson: LoadSongsFromJsonUseCase,
    val isDatabaseEmpty: IsDatabaseEmptyUseCase
)