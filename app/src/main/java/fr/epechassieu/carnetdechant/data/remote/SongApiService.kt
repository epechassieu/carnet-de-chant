package fr.epechassieu.carnetdechant.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class SongApiService @Inject constructor(
    private val httpClient: HttpClient
) {

    companion object {
        private const val SONGS_URL = "https://raw.githubusercontent.com/epechassieu/chants_eglise/chants.json"
    }

    suspend fun getSongs(): SongsResponseDto {
        return httpClient.get(SONGS_URL).body()
    }
}