package fr.epechassieu.carnetdechant.usecasestest

import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import fr.epechassieu.carnetdechant.domain.usecases.GetSongsByCategoryUseCase
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetSongsByCategoryUseCaseTest {

    private val songRepository: SongRepository = mockk()
    private lateinit var getSongsByCategoryUseCase: GetSongsByCategoryUseCase

    //initialize data
    private val song1 = Song(
        "1",
        "JEM",
        number = 100,
        title = "titre1",
        categories = listOf(Category.ADORATION, Category.LOUANGE),
        lyrics = "ceci est le chant 1",
        urlMedia = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
    )
    private val song2 = Song(
        "2",
        "JEM",
        number = 200,
        title = "titre2",
        categories = listOf(Category.LOUANGE),
        lyrics = "cela est le chant 2",
        urlMedia = "https://www.youtube.com/watch?v=123"
    )
    private val song3 = Song(
        "3",
        "JEM",
        number = 300,
        title = "titre3",
        categories = listOf(Category.APPEL),
        lyrics = "cela est le chant 3",
        urlMedia = "https://www.youtube.com/watch?v=1234"
    )
    private val songsList = listOf(song1, song2,song3)

    @Before
    fun setup() {
        clearMocks(songRepository)
        getSongsByCategoryUseCase = GetSongsByCategoryUseCase(songRepository)
    }

    @Test
    fun `invoke with category having songs returns list of songs`() = runTest {
        val category = Category.LOUANGE
        val expectedSongs = listOf(song1,song2,song3)
        every { songRepository.getSongsByCategory(category) } returns flowOf(expectedSongs)

        val result = getSongsByCategoryUseCase.invoke(category).first()
        println("DEBUG test result est : $result")

        assertEquals(songsList, result)
        verify { songRepository.getSongsByCategory(category) }
    }

    @Test
    fun `invoke passes correct category to repository`() = runTest {

        val capturedCategory = slot<Category>()
        every { songRepository.getSongsByCategory(capture(capturedCategory)) } returns flowOf(listOf(song3))

        getSongsByCategoryUseCase.invoke(Category.LOUANGE).first()
        println("DEBUG test correctcategory est : $capturedCategory")


        assertEquals(Category.LOUANGE, capturedCategory.captured)
    }

    @Test
    fun `invoke with category having no songs returns empty list`() = runTest {

        every { songRepository.getSongsByCategory(Category.PARDON) } returns flowOf(emptyList())

        val result = getSongsByCategoryUseCase.invoke(Category.PARDON).first()

        assertTrue(result.isEmpty())
        verify { songRepository.getSongsByCategory(Category.PARDON) }
    }


}