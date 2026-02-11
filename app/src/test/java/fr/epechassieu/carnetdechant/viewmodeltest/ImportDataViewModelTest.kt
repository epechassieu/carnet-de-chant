package fr.epechassieu.carnetdechant.viewmodeltest

import android.content.Context
import app.cash.turbine.test
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import fr.epechassieu.carnetdechant.R
import fr.epechassieu.carnetdechant.domain.exception.AppException
import fr.epechassieu.carnetdechant.ui.importdata.ImportDataUiState
import fr.epechassieu.carnetdechant.ui.importdata.ImportDataViewModel
import fr.epechassieu.carnetdechant.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ImportDataViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val songRepository: SongRepository = mockk()
    private val context: Context = mockk()

    private lateinit var viewModel: ImportDataViewModel

    @Before
    fun setup() {
        // Préparation du context pour les messages courants
        every { context.getString(any()) } returns "Message d'erreur"
        every { context.getString(any(), any()) } returns "Message avec paramètres"
    }

    private fun createViewModel() = ImportDataViewModel(songRepository, context)

    @Test
    fun `initial state should be Idle`() = runTest {
        viewModel = createViewModel()
        assertEquals(ImportDataUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `importSongs success should emit Loading then Success`() = runTest {
        // GIVEN
        val count = 266
        val successMessage = "266 chants importés"
        coEvery { songRepository.loadSongsFromJson() } coAnswers {
            delay(100)
            Result.success(count)
        }
        every { context.getString(R.string.import_success, count) } returns successMessage

        viewModel = createViewModel()

        // THEN
        viewModel.uiState.test {
            assertEquals(ImportDataUiState.Idle, awaitItem())

            // WHEN
            viewModel.importSongs()

            assertEquals(ImportDataUiState.Loading, awaitItem())

            val finalState = awaitItem()
            assert(finalState is ImportDataUiState.Success)

        }
    }

    @Test
    fun `importSongs network failure should emit Loading then Error`() = runTest {
        // GIVEN
        val networkErrorMessage = "Pas de réseau"
        coEvery { songRepository.loadSongsFromJson() } coAnswers {
            delay(100)
            Result.failure(AppException.NetworkError)
        }
        every { context.getString(R.string.error_network) } returns networkErrorMessage

        viewModel = createViewModel()

        // THEN
        viewModel.uiState.test {
            skipItems(1) // On saute Idle

            // WHEN
            viewModel.importSongs()

            assertEquals(ImportDataUiState.Loading, awaitItem())

            val finalState = awaitItem()
            assert(finalState is ImportDataUiState.Error)
        }
    }
}