package fr.epechassieu.carnetdechant.usecasestest

import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser
import fr.epechassieu.carnetdechant.domain.repository.UrlMediaUserRepository
import fr.epechassieu.carnetdechant.domain.usecases.GetUrlMediaUserBySongIdUseCase
import io.mockk.clearMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GetUrlMediaUserBySongIdUseCaseTest {
    private val urlMediaUserRepository: UrlMediaUserRepository = mockk()
    private lateinit var getUrlMediaUserBySongIdUseCase: GetUrlMediaUserBySongIdUseCase

    @Before
    fun setUp() {
        clearMocks(urlMediaUserRepository)
        getUrlMediaUserBySongIdUseCase = GetUrlMediaUserBySongIdUseCase(urlMediaUserRepository)
    }


    @Test
    fun `invoke should retun list of media for a  specific songid`() = runTest {
        val songId = "1"
        val media1 = UrlMediaUser(id = 1, songId = songId, url = "https:\\youtube.com\\watch?v=123")
        val media2 = UrlMediaUser(id = 2, songId = songId, url = "https:\\youtube.com\\watch?v=456")
        val expectedList = listOf(media1, media2)

        every { urlMediaUserRepository.getUrlMediaUserBySongId(songId) } returns flowOf(expectedList)


        val result = getUrlMediaUserBySongIdUseCase.invoke(songId).first()

        Assert.assertEquals(2, result.size)
        Assert.assertEquals("https:\\youtube.com\\watch?v=456", result[1].url)
        verify{ urlMediaUserRepository.getUrlMediaUserBySongId(songId) }
        confirmVerified(urlMediaUserRepository)
    }

    @Test
    fun `invoke should call repository once and emit empty list when song does not exist`()=runTest {
        val songId = "1"

        every { urlMediaUserRepository.getUrlMediaUserBySongId(songId) } returns flowOf(emptyList())

        val result = getUrlMediaUserBySongIdUseCase.invoke(songId).first()

        Assert.assertEquals(0, result.size)
        verify{ urlMediaUserRepository.getUrlMediaUserBySongId(songId) }
        confirmVerified(urlMediaUserRepository)

    }

}