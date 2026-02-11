package fr.epechassieu.carnetdechant.usecasestest.usecaseTest

import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import fr.epechassieu.carnetdechant.domain.usecases.GetSongByIdUseCase
import io.mockk.clearMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GetSongByIdUseCaseTest {

    private val songRepository: SongRepository = mockk()
    private lateinit var getSongByIdUseCase: GetSongByIdUseCase

    @Before
    fun setUp() {
        clearMocks(songRepository)
        getSongByIdUseCase = GetSongByIdUseCase(songRepository)
    }


    @Test
    fun `invoke should return the song form repository when id is provided`() = runTest {
        val id = "1"
        val expectedSong = Song(
            id,
            songbook = "JEM",
            number = 100,
            title = "Dieu est grand",
            categories = listOf(Category.LOUANGE),
            lyrics = "Test",
            urlMedia = ""
        )

        every { songRepository.getSongById((id)) } returns flowOf(expectedSong)

        val result = getSongByIdUseCase.invoke(id).first()

        Assert.assertEquals(expectedSong, result)
        verify(exactly = 1) { songRepository.getSongById(id) }
        confirmVerified(songRepository)
    }

    @Test
    fun `invoke should call repository once and emit null when song does not exist`() = runTest {
        val id = "1"

        every { songRepository.getSongById((id)) } returns flowOf(null)

        val result = getSongByIdUseCase.invoke(id).first()

        // THEN
        Assert.assertEquals(null, result)
        verify(exactly = 1) { songRepository.getSongById(id) }
        confirmVerified(songRepository)
    }
}