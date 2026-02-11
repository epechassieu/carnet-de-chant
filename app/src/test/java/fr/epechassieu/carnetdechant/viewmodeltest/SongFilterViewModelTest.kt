package fr.epechassieu.carnetdechant.viewmodeltest

import android.content.Context
import app.cash.turbine.test
import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.R
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.usecases.GetSongsByCategoryUseCase
import fr.epechassieu.carnetdechant.ui.songfilter.SongFilterViewModel
import fr.epechassieu.carnetdechant.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SongFilterViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SongFilterViewModel

    // --- Mock dependencies ---
    private val getSongsByCategoryUseCase: GetSongsByCategoryUseCase = mockk()
    private val context: Context = mockk(relaxed = true)

    // --- Test data ---
    private val song1 = Song(
        id = "1",
        songbook = "JEM",
        number = 100,
        title = "Chant de louange",
        categories = listOf(Category.LOUANGE),
        lyrics = "Paroles louange",
        urlMedia = ""
    )
    private val song2 = Song(
        id = "2",
        songbook = "ATG",
        number = 200,
        title = "Chant d'adoration",
        categories = listOf(Category.ADORATION),
        lyrics = "Paroles adoration",
        urlMedia = ""
    )
    private val louangeSongs = listOf(song1)
    private val adorationSongs = listOf(song2)

    private val expectedCategories = Category.entries.filter { it != Category.INCONNU }

    @Before
    fun setup() {
        every { context.getString(any()) } returns "Error message"
        every { context.getString(any(), any()) } returns "Error message with param"

        every { getSongsByCategoryUseCase(Category.LOUANGE) } returns flowOf(louangeSongs)
        every { getSongsByCategoryUseCase(Category.ADORATION) } returns flowOf(adorationSongs)
    }

    private fun createViewModel(): SongFilterViewModel {
        return SongFilterViewModel(getSongsByCategoryUseCase, context)
    }

    // --- Test initial state ---

    @Test
    fun `initial state should show categories without INCONNU`() = runTest {
        viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()

            assertEquals(expectedCategories, state.categories)
            assertFalse(state.categories.contains(Category.INCONNU))
            assertNull(state.selectedCategory)
            assertTrue(state.filteredSongs.isEmpty())
            assertNull(state.error)
            cancelAndConsumeRemainingEvents()
        }
    }

    // --- Tests select category ---

    @Test
    fun `selectCategory should update selectedCategory`() = runTest {
        viewModel = createViewModel()

        viewModel.selectCategory(Category.LOUANGE)

        viewModel.uiState.test {
            val state = awaitItem()

            assertEquals(Category.LOUANGE, state.selectedCategory)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `selectCategory should load filtered songs`() = runTest {
        viewModel = createViewModel()

        viewModel.selectCategory(Category.LOUANGE)

        viewModel.uiState.test {
            val state = awaitItem()

            assertEquals(louangeSongs, state.filteredSongs)
            assertEquals(1, state.filteredSongs.size)
            assertEquals("Chant de louange", state.filteredSongs[0].title)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `selectCategory should call use case with correct category`() = runTest {
        viewModel = createViewModel()

        viewModel.selectCategory(Category.ADORATION)

        viewModel.uiState.test {
            awaitItem()
            verify { getSongsByCategoryUseCase(Category.ADORATION) }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `changing category should load new songs`() = runTest {
        viewModel = createViewModel()

        viewModel.selectCategory(Category.LOUANGE)

        viewModel.uiState.test {
            val state1 = awaitItem()
            assertEquals(Category.LOUANGE, state1.selectedCategory)
            assertEquals(louangeSongs, state1.filteredSongs)
            cancelAndConsumeRemainingEvents()
        }

        viewModel.selectCategory(Category.ADORATION)

        viewModel.uiState.test {
            val state2 = awaitItem()
            assertEquals(Category.ADORATION, state2.selectedCategory)
            assertEquals(adorationSongs, state2.filteredSongs)
            cancelAndConsumeRemainingEvents()
        }
    }

    // --- clear select ---

    @Test
    fun `clearSelection should reset to initial state`() = runTest {
        viewModel = createViewModel()

        viewModel.selectCategory(Category.LOUANGE)

        viewModel.uiState.test {
            val stateWithSelection = awaitItem()
            assertEquals(Category.LOUANGE, stateWithSelection.selectedCategory)
            cancelAndConsumeRemainingEvents()
        }

        viewModel.clearSelection()

        viewModel.uiState.test {
            val stateCleared = awaitItem()

            assertNull(stateCleared.selectedCategory)
            assertTrue(stateCleared.filteredSongs.isEmpty())
            assertEquals(expectedCategories, stateCleared.categories)
            cancelAndConsumeRemainingEvents()
        }
    }

    // -- not category selected ---

    @Test
    fun `selectCategory should show empty list when category has no songs`() = runTest {
        every { getSongsByCategoryUseCase(Category.APPEL) } returns flowOf(emptyList())

        viewModel = createViewModel()

        viewModel.selectCategory(Category.APPEL)

        viewModel.uiState.test {
            val state = awaitItem()

            assertEquals(Category.APPEL, state.selectedCategory)
            assertTrue(state.filteredSongs.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    // --- Test error ---

    @Test
    fun `uiState should show error when use case fails`() = runTest {
        every { getSongsByCategoryUseCase(Category.LOUANGE) } returns flow {
            throw Exception("Database error")
        }
        every { context.getString(R.string.error_database) } returns "Erreur base de données"

        viewModel = createViewModel()

        viewModel.selectCategory(Category.LOUANGE)

        viewModel.uiState.test {
            val state = awaitItem()

            assertEquals("Erreur base de données", state.error)
            assertEquals(expectedCategories, state.categories)
            cancelAndConsumeRemainingEvents()
        }
    }
}