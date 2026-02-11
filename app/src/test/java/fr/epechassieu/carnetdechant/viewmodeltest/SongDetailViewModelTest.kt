package fr.epechassieu.carnetdechant.viewmodeltest

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.R
import fr.epechassieu.carnetdechant.domain.usecases.GetSongByIdUseCase
import fr.epechassieu.carnetdechant.ui.songdetail.SongDetailUiState
import fr.epechassieu.carnetdechant.ui.songdetail.SongDetailViewModel
import fr.epechassieu.carnetdechant.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SongDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getSongByIdUseCase: GetSongByIdUseCase = mockk()
    private val context: Context = mockk()
    private val songId = "456"
    private val savedStateHandle = SavedStateHandle(mapOf("songId" to songId))

    private val mockSong = Song(
        id = songId,
        songbook = "JEM",
        number = 456,
        title = "Chant de Test",
        categories = emptyList(),
        lyrics = "Paroles...",
        urlMedia = "http://test.com"
    )

    @Before
    fun setup() {
        every { context.getString(R.string.error_song_not_found) } returns "Chant introuvable"
    }

    private fun createViewModel() =
        SongDetailViewModel(getSongByIdUseCase, savedStateHandle, context)

    @Test
    fun `state should emit success when song is found`() = runTest {

        every { getSongByIdUseCase(songId) } returns flowOf(mockSong)
        val viewModel = createViewModel()


        viewModel.uiState.test {
            val state = expectMostRecentItem()

            assert(state is SongDetailUiState.Success)
            val successState = state as SongDetailUiState.Success
            assertEquals("Chant de Test", successState.song.title)
        }
    }

    @Test
    fun `state should emit error when song is null`() = runTest {

        every { getSongByIdUseCase(songId) } returns flowOf(null)
        val viewModel = createViewModel()


        viewModel.uiState.test {
            val state = expectMostRecentItem()

            assert(state is SongDetailUiState.Error)
            assertEquals("Chant introuvable", (state as SongDetailUiState.Error).message)
        }
    }
}