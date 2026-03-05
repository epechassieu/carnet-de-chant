package fr.epechassieu.carnetdechant.viewmodeltest

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import fr.epechassieu.carnetdechant.domain.exception.AppException
import fr.epechassieu.carnetdechant.R
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.usecases.GetSongByIdUseCase
import fr.epechassieu.carnetdechant.ui.listen.ListenViewModel
import fr.epechassieu.carnetdechant.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class ListenViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ListenViewModel

    // ---  Mock dependancies ---
    private val getSongByIdUseCase: GetSongByIdUseCase = mockk()
    private val getUrlMediaUserBySongIdUseCase: GetUrlMediaUserBySongIdUseCase = mockk()
    private val addUrlMediaUserUseCase: AddUrlMediaUserUseCase = mockk()
    private val deleteUrlMediaUserUseCase: DeleteUrlMediaUserUseCase = mockk()
    private val context: Context = mockk()

    // --- simulate songId ---
    private val songId = "123"
    private val savedStateHandle = SavedStateHandle(mapOf("songId" to songId))

    private val mockSong = Song(
        id = songId,
        songbook = "JEM",
        number = 200,
        title = "chant 200",
        categories = emptyList(),
        lyrics = "Paroles du chant",
        urlMedia = "http://official.com"
    )
    private val mockUserUrls = listOf(
        UrlMediaUser(id = 1, songId = songId, url = "http://user.com")
    )

    @Before
    fun setup() {
        // --- mock use cases ---
        every { getSongByIdUseCase(songId) } returns flowOf(mockSong)
        every { getUrlMediaUserBySongIdUseCase(songId) } returns flowOf(mockUserUrls)
        //--- mock context string ---
        every { context.getString(any()) } returns "Error message"
        every { context.getString(any(), any()) } returns "Error message with param"
    }

    private fun createViewModel(): ListenViewModel {
        return ListenViewModel(
            getSongByIdUseCase,
            getUrlMediaUserBySongIdUseCase,
            addUrlMediaUserUseCase,
            deleteUrlMediaUserUseCase,
            savedStateHandle,
            context
        )
    }


    // --- test UiState ---
    @Test
    fun `uiState combine song, user urls and error correctly`() = runTest {
        viewModel = createViewModel()

        // -- then --
        viewModel.uiState.test {

            val state = expectMostRecentItem()

            assertEquals("chant 200", state.songTitle)
            assertEquals("http://official.com", state.officialUrl)
            assertEquals(mockUserUrls, state.userUrls)
            assertFalse(state.isLoading)
            assertNull(state.error)

        }
    }

    @Test
    fun `uiState should show error when song not found`() = runTest {
        every { getSongByIdUseCase(songId) } returns flowOf(null)
        every { context.getString(R.string.error_song_not_found) } returns "song not found"

        viewModel = createViewModel()

        viewModel.uiState.test {
            val state = expectMostRecentItem()

            assertEquals("song not found", state.error)
            assertFalse(state.isLoading)
        }
    }

    // --- Tests Add url ---
    @Test
    fun `addUrl should call use case with correct data`() = runTest {

        coEvery { addUrlMediaUserUseCase(any()) } returns Result.success(Unit)
        viewModel = createViewModel()

        viewModel.onNewUrlTextChange("https://new-url.com")
        viewModel.addUrl()

        coVerify { addUrlMediaUserUseCase(match { it.url == "https://new-url.com" && it.songId == songId }) }
    }

    @Test
    fun `addUrl should clear text field on success`() = runTest {
        coEvery { addUrlMediaUserUseCase(any()) } returns Result.success(Unit)
        viewModel = createViewModel()

        viewModel.onNewUrlTextChange("https://new-url.com")
        viewModel.addUrl()

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals("", state.newUrlText)
        }
    }

    @Test
    fun `addUrl should not call use case when url is blank`() = runTest {
        viewModel = createViewModel()

        viewModel.onNewUrlTextChange("")
        viewModel.addUrl()

        coVerify(exactly = 0) { addUrlMediaUserUseCase(any()) }
    }

    @Test
    fun `addUrl should set error on failure`() = runTest {
        val errorMessage = "Error database"
        every { context.getString(R.string.error_database) } returns errorMessage
        coEvery { addUrlMediaUserUseCase(any()) } returns Result.failure(AppException.DatabaseError())

        viewModel = createViewModel()

        viewModel.onNewUrlTextChange("https://new-url.com")
        viewModel.addUrl()

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals(errorMessage, state.error)
        }
    }

    // --- tests delete url ---

    @Test
    fun `deleteUrl should call use case`() = runTest {
        coEvery { deleteUrlMediaUserUseCase(any()) } returns Result.success(Unit)
        viewModel = createViewModel()

        val urlToDelete = UrlMediaUser(id = 1, songId = songId, url = "http://test.com")
        viewModel.deleteUrl(urlToDelete)

        coVerify { deleteUrlMediaUserUseCase(urlToDelete) }
    }

    @Test
    fun `deleteUrl should set error on failure`() = runTest {
        val errorMessage = "database error"
        every { context.getString(R.string.error_database) } returns errorMessage
        coEvery { deleteUrlMediaUserUseCase(any()) } returns Result.failure(AppException.DatabaseError())

        viewModel = createViewModel()

        val urlToDelete = UrlMediaUser(id = 1, songId = songId, url = "http://test.com")
        viewModel.deleteUrl(urlToDelete)

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals(errorMessage, state.error)
        }
    }

    // --- Test Clear error ---

    @Test
    fun `clearError should reset error to null`() = runTest {
        val errorMessage = "ERROR"
        every { context.getString(R.string.error_database) } returns errorMessage
        coEvery { deleteUrlMediaUserUseCase(any()) } returns Result.failure(AppException.DatabaseError())

        viewModel = createViewModel()

        // cause error
        val urlToDelete = UrlMediaUser(id = 1, songId = songId, url = "http://test.com")
        viewModel.deleteUrl(urlToDelete)

        // check error state
        viewModel.uiState.test {
            val stateWithError = expectMostRecentItem()
            assertEquals(errorMessage, stateWithError.error)
        }

        // clear error
        viewModel.clearError()

        // check error state null
        viewModel.uiState.test {
            val stateCleared = expectMostRecentItem()
            assertNull(stateCleared.error)
        }
    }

    // --- Tests new url text ---
    @Test
    fun `onNewUrlTextChange should update newUrlText in state`() = runTest {
        viewModel = createViewModel()

        viewModel.onNewUrlTextChange("https://example.com")

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals("https://example.com", state.newUrlText)
        }
    }
}



