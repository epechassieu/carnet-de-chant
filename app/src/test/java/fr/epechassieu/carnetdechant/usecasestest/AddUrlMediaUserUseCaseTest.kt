package fr.epechassieu.carnetdechant.usecasestest.usecaseTest

import fr.epechassieu.carnetdechant.domain.exception.AppException
import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser
import fr.epechassieu.carnetdechant.domain.repository.UrlMediaUserRepository
import fr.epechassieu.carnetdechant.domain.usecases.AddUrlMediaUserUseCase
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddUrlMediaUserUseCaseTest {

    private val urlMediaUserRepository: UrlMediaUserRepository = mockk()
    private lateinit var addUrlMediaUserUseCase: AddUrlMediaUserUseCase

    @Before
    fun setUp() {
        clearMocks(urlMediaUserRepository)
        addUrlMediaUserUseCase = AddUrlMediaUserUseCase(urlMediaUserRepository)
        }

@Test
    fun `invoke should return success when repository success`()=runTest {

        val urlItem= UrlMediaUser(id = 1, songId = "1", url = "https://youtube.com/watch?v=123")

        coEvery { urlMediaUserRepository.addUrlMediaUser(urlItem) } returns Result.success(Unit)

        val result = addUrlMediaUserUseCase.invoke(urlItem)
        println("result 1 : $result")

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { urlMediaUserRepository.addUrlMediaUser(urlItem) }

    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // GIVEN
        val urlItem = UrlMediaUser(id = 1, songId = "1", url = "http://test.com")
        val expectedError = AppException.DatabaseError

        //  simule un échec avec  exception personnalisée
        coEvery { urlMediaUserRepository.addUrlMediaUser(urlItem) } returns Result.failure(expectedError)

        // WHEN
        val result = addUrlMediaUserUseCase.invoke(urlItem)
        println("result 2 : $result")

        // THEN
        assertTrue(result.isFailure) // On vérifie que ça a échoué
        assertEquals(expectedError, result.exceptionOrNull()) // On vérifie que c'est la bonne erreur
    }
}