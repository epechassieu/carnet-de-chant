package fr.epechassieu.carnetdechant.viewmodeltest

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import fr.epechassieu.carnetdechant.domain.exception.AppException
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser
import fr.epechassieu.carnetdechant.domain.usecases.AddUrlMediaUserUseCase
import fr.epechassieu.carnetdechant.domain.usecases.DeleteUrlMediaUserUseCase
import fr.epechassieu.carnetdechant.domain.usecases.GetSongByIdUseCase
import fr.epechassieu.carnetdechant.domain.usecases.GetUrlMediaUserBySongIdUseCase
import fr.epechassieu.carnetdechant.ui.listen.ListenViewModel
import fr.epechassieu.carnetdechant.util.MainDispatcherRule
import fr.epechassieu.carnetdechant.R
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test


class ListenViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ListenViewModel

    // ---  Mock des dépendances ---
    private val getSongByIdUseCase: GetSongByIdUseCase = mockk()
    private val getUrlMediaUserBySongIdUseCase: GetUrlMediaUserBySongIdUseCase = mockk()
    private val addUrlMediaUserUseCase: AddUrlMediaUserUseCase = mockk()
    private val deleteUrlMediaUserUseCase: DeleteUrlMediaUserUseCase = mockk()
    private val context: Context = mockk()

    // --- simulate songId ---
    private val savedStateHandle = SavedStateHandle(mapOf("songId" to "123"))

    // --- test d'ajout ---
    @Test
    fun `addUrl should call use case`() = runTest {
        every { getSongByIdUseCase("123") } returns flowOf(null)
        every { getUrlMediaUserBySongIdUseCase("123") } returns flowOf(emptyList())

        viewModel = ListenViewModel(
            getSongByIdUseCase,
            getUrlMediaUserBySongIdUseCase,
            addUrlMediaUserUseCase,
            deleteUrlMediaUserUseCase,
            savedStateHandle,
            context
        )

        val newurl = "https://test.com"
        coEvery { addUrlMediaUserUseCase(any()) } returns Result.success(Unit)

        viewModel.addUrl(newurl)

        coVerify { addUrlMediaUserUseCase(match { it.url == newurl && it.songId == "123" }) }
    }

    // --- test de l'état final ---
    @Test
    fun `uiState combine song, user urls and error correctly`() = runTest {
        val songId = "123"
        val mockSong = Song(
            songId,
            "JEM",
            200,
            "chant 200",
            emptyList(),
            "parole chant 200",
            "http://official.com"
        )
        val mockUserUrls = listOf(UrlMediaUser(id = 1, songId = songId, url = "http://user.com"))


        every { getSongByIdUseCase(songId) } returns flowOf(mockSong)
        every { getUrlMediaUserBySongIdUseCase(songId) } returns flowOf(mockUserUrls)

        viewModel = ListenViewModel(
            getSongByIdUseCase,
            getUrlMediaUserBySongIdUseCase,
            addUrlMediaUserUseCase,
            deleteUrlMediaUserUseCase,
            savedStateHandle,
            context
        )

        // -- then --
        viewModel.uiState.test {

            val finalState = expectMostRecentItem()
            println("DEBUG finalState est : $finalState")
            assertEquals("chant 200", finalState.songTitle)
            assertEquals(mockUserUrls, finalState.userUrls)
            assertEquals("http://official.com", finalState.officialUrl)
            assertNull(finalState.error)
            assertFalse(finalState.isLoading)
        }
    }

    // --- test de suppression ---
    @Test
    fun `deleteUrl should call use case and clear error`() = runTest {
        every { getSongByIdUseCase("123") } returns flowOf(null)
        every { getUrlMediaUserBySongIdUseCase("123") } returns flowOf(emptyList())
        viewModel = ListenViewModel(
            getSongByIdUseCase,
            getUrlMediaUserBySongIdUseCase,
            addUrlMediaUserUseCase,
            deleteUrlMediaUserUseCase,
            savedStateHandle,
            context
        )
        val urlMediaUserToDelete = UrlMediaUser(id = 1, songId = "123", url = "http://test.com")
        println("DEBUG urlMediaUserToDelete est : $urlMediaUserToDelete")

        coEvery { deleteUrlMediaUserUseCase(urlMediaUserToDelete) } returns Result.success(Unit)

        viewModel.deleteUrl(urlMediaUserToDelete)

        coVerify { deleteUrlMediaUserUseCase(urlMediaUserToDelete) }

    }

    // --- la suppression échoue ---
    @Test
    fun `deleteUrl failure should set error`() = runTest {
        val urlToDelete = UrlMediaUser(id = 1, songId = "123", url = "http://test.com")
        val erroMessage = "Error Database"

        every { getSongByIdUseCase("123") } returns flowOf(null)
        every { getUrlMediaUserBySongIdUseCase("123") } returns flowOf(emptyList())

        coEvery { deleteUrlMediaUserUseCase(urlToDelete) } returns Result.failure(AppException.DatabaseError)
        every { context.getString(any()) } returns erroMessage

        viewModel = ListenViewModel(
            getSongByIdUseCase,
            getUrlMediaUserBySongIdUseCase,
            addUrlMediaUserUseCase,
            deleteUrlMediaUserUseCase,
            savedStateHandle,
            context
        )

        viewModel.uiState.test {
            /*val state = expectMostRecentItem()*/
            val initialstagte=awaitItem()
            println("DEBUG initialstate est : $initialstagte")


            viewModel.deleteUrl(urlToDelete)
            val stateError = awaitItem()
            println("DEBUG state est : $stateError")

            assertEquals(erroMessage, stateError.error)
        }
    }


}



