package fr.epechassieu.carnetdechant.usecasestest

import fr.epechassieu.carnetdechant.domain.exception.AppException
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import fr.epechassieu.carnetdechant.domain.usecases.LoadSongsFromJsonUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoadSongsFromJsonUseCaseTest {

    val songRepository: SongRepository = mockk()
    val loadSongsFromJsonUseCase = LoadSongsFromJsonUseCase(songRepository)

    @Test

    fun `invoke should return success when repository`() = runTest {

        coEvery { songRepository.loadSongsFromJson() } returns Result.success(10)

        val result = loadSongsFromJsonUseCase.invoke()

        assertTrue(result.isSuccess)
        assertEquals(10, result.getOrNull())
        coVerify(exactly = 1) { songRepository.loadSongsFromJson() }
    }

    @Test
    fun `invoke should return failure from repository`() = runTest {
        // GIVEN
        val networkError = AppException.NetworkError()
        coEvery { songRepository.loadSongsFromJson() } returns Result.failure(networkError)

        // WHEN
        val result = loadSongsFromJsonUseCase.invoke()

        // THEN
        assertTrue(result.isFailure)
        assertEquals(networkError, result.exceptionOrNull())
    }

}