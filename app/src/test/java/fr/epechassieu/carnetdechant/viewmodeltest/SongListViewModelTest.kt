package fr.epechassieu.carnetdechant.viewmodeltest

import android.content.Context
import app.cash.turbine.test
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.usecases.GetSongsByTitleUseCase
import fr.epechassieu.carnetdechant.ui.songlist.SongListUiState
import fr.epechassieu.carnetdechant.ui.songlist.SongListViewModel
import fr.epechassieu.carnetdechant.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SongListViewModelTest {
    // --- initialize dispatcher ---
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SongListViewModel

    // --- Mock dependancies ---

    private val getSongsByTitleUseCase: GetSongsByTitleUseCase = mockk()
    private val context: Context = mockk(relaxed = true)

    // --- test data ---
    private val song1 = Song(
        "1",
        "JEM",
        number = 100,
        title = "titre1",
        categories = emptyList(),
        lyrics = "ceci est le chant 1",
        urlMedia = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
    )
    private val song2 = Song(
        "2",
        "JEM",
        number = 200,
        title = "titre2",
        categories = emptyList(),
        lyrics = "cela est le chant 2",
        urlMedia = "https://www.youtube.com/watch?v=123"
    )
    private val mockListSongs = listOf(song1, song2)

    @Before
    fun setup() {
        // --  mocks --
        every { context.getString(any()) } returns "Error message"
        every { context.getString(any(), any()) } returns "Error message with param"

        every { getSongsByTitleUseCase() } returns flowOf(mockListSongs)
    }
    private fun createViewModel(): SongListViewModel {
        return SongListViewModel(getSongsByTitleUseCase, context)
    }

    // --- Test ui state ---
    @Test
    fun `uiState should show all songs when search query is empty`() = runTest {
        viewModel= createViewModel()

        viewModel.uiState.test {
            val state = awaitItem() as SongListUiState.Success
            assertEquals(2, state.songs.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    // --- Test search title ---
    @Test
    fun `uiState should filter list when search query is a song title`() = runTest {
        viewModel = createViewModel()

        viewModel.onSearchQueryChange("titre2")

        viewModel.uiState.test {
            val state = awaitItem() as SongListUiState.Success
            assertEquals(1, state.songs.size)
            assertEquals("titre2", state.songs[0].title)
            cancelAndConsumeRemainingEvents()
        }
    }

    // --- Test search number ---
    @Test
    fun `uiState should filter list when search query is a song number`() = runTest {
        viewModel = createViewModel()

        viewModel.onSearchQueryChange("200")

        viewModel.uiState.test {
            val state = awaitItem() as SongListUiState.Success
            assertEquals(1, state.songs.size)
            assertEquals(200, state.songs[0].number)
            cancelAndConsumeRemainingEvents()
        }

    }

    // --- Test search lyrics ---
    @Test
    fun `uiState should filter list when search query is a song lyrics`() = runTest {
        viewModel = createViewModel()

        viewModel.onSearchQueryChange("cela")

        viewModel.uiState.test {
            val state = awaitItem() as SongListUiState.Success
            assertEquals(1, state.songs.size)
            assertEquals(song2, state.songs[0])
            cancelAndConsumeRemainingEvents()
        }
    }

    // --- test search no song is found ---
    @Test
    fun `uiState should return empty list when no match`() = runTest {
        viewModel = createViewModel()

        viewModel.onSearchQueryChange("inexistant")

        viewModel.uiState.test {
            val state = awaitItem() as SongListUiState.Success
            assertTrue(state.songs.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    // --- test search query ---
    @Test
    fun `onSearchQueryChange should update searchQuery`() = runTest {
        viewModel = createViewModel()

        viewModel.onSearchQueryChange("test")

        assertEquals("test", viewModel.searchQuery.value)
    }

    // --- test error ---
    @Test
    fun `uiState should show error when use case fails`() = runTest {

        every { getSongsByTitleUseCase() } returns flow { throw Exception("Database crash") }
        every { context.getString(any()) } returns "database error"

        viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is SongListUiState.Error)
            assertEquals("database error", (state as SongListUiState.Error).message)
            cancelAndConsumeRemainingEvents()
        }
    }
}


