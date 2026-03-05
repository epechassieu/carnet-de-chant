package fr.epechassieu.carnetdechant.usecasestest

import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import fr.epechassieu.carnetdechant.domain.usecases.GetSongsByTitleUseCase
import io.mockk.clearMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetSongsByTitleUseCaseTest {

    private val songRepository: SongRepository = mockk()
    private lateinit var getSongByTitleUseCase: GetSongsByTitleUseCase

    //initialize data
    private val song1 = Song(
        "1",
        "JEM",
        number = 100,
        title = "titre1",
        categories = emptyList(),
        lyrics = "ceci est le chant 1",
        audio = "https://www.youtube.com/v123"
    )
    private val song2 = Song(
        "2",
        "JEM",
        number = 200,
        title = "titre2",
        categories = emptyList(),
        lyrics = "cela est le chant 2",
        audio = "https://www.youtube.com/v234"

    )
    private val songsList = listOf(song1, song2)

    @Before
    fun setup() {
        clearMocks(songRepository)
        getSongByTitleUseCase = GetSongsByTitleUseCase(songRepository)
    }

    @Test
    fun `invoke should call repository once and return list of songs from repository`() = runTest {
        every { songRepository.getSongsByTitle() } returns flowOf(songsList)


        val result = getSongByTitleUseCase.invoke().first()

        Assert.assertEquals(songsList, result)
        verify(exactly = 1) { songRepository.getSongsByTitle() }
        confirmVerified(songRepository)
    }

    @Test
    fun `invoke should call repository once and emit empty list when no song exists`() = runTest {
        every { songRepository.getSongsByTitle() } returns flowOf(emptyList())

        val result = getSongByTitleUseCase.invoke().first()

        assertTrue(result.isEmpty())
        verify(exactly = 1) { songRepository.getSongsByTitle() }
        confirmVerified(songRepository)

    }

}