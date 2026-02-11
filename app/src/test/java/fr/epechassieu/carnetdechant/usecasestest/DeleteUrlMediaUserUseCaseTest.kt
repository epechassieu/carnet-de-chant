package fr.epechassieu.carnetdechant.usecasestest

import fr.epechassieu.carnetdechant.domain.exception.AppException
import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser
import fr.epechassieu.carnetdechant.domain.repository.UrlMediaUserRepository
import fr.epechassieu.carnetdechant.domain.usecases.DeleteUrlMediaUserUseCase
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteUrlMediaUserUseCaseTest {
    private val urlMediaUserRepository: UrlMediaUserRepository = mockk()
    private lateinit var deleteUrlMediaUserUseCase: DeleteUrlMediaUserUseCase

    @Before
    fun setUp() {
        clearMocks(urlMediaUserRepository)
        deleteUrlMediaUserUseCase = DeleteUrlMediaUserUseCase(urlMediaUserRepository)
    }

    @Test
    fun `invoke should return success when repository successfully delete`() = runTest {

        val urlItem = UrlMediaUser(id = 1, songId = "1", url = "https://youtube.com/watch?v=123")

        coEvery { urlMediaUserRepository.deleteUrlMediaUser(urlItem) } returns Result.success(Unit)

        val result = deleteUrlMediaUserUseCase.invoke(urlItem)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { urlMediaUserRepository.deleteUrlMediaUser(urlItem) }

    }

    @Test
    fun `invoke should return failure when repository fails to delete`() = runTest {
        // GIVEN
        val urlItem = UrlMediaUser(id = 1, songId = "1", url = "http://test.com")
        val expectedError = AppException.DatabaseError

        coEvery { urlMediaUserRepository.deleteUrlMediaUser(urlItem) } returns Result.failure(
            expectedError
        )

        // WHEN
        val result = deleteUrlMediaUserUseCase.invoke(urlItem)

        // THEN
        assertTrue(result.isFailure)
        assertEquals(
            expectedError,
            result.exceptionOrNull()
        )
    }
}