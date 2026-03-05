package fr.epechassieu.carnetdechant.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

/**
 * Service responsible for fetching song data from a remote repository.
 *
 * This class uses a [HttpClient] to perform network requests and retrieve a list
 * of songs in JSON format from a predefined GitHub URL.
 *
 * @property httpClient The Ktor HTTP client used to perform network requests.
 */
class SongApiService @Inject constructor(
    private val httpClient: HttpClient
) {

    companion object {
        private const val SONGS_URL = "https://chants.epechassieu.fr/chants.json"
    }

    suspend fun getSongs(): SongsResponseDto {
        return httpClient.get(SONGS_URL).body()
    }
}