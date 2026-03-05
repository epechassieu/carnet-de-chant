package fr.epechassieu.carnetdechant.data.remote

import io.ktor.client.HttpClient
import javax.inject.Inject


class AudioApiService @Inject constructor(
    private val httpClient: HttpClient
) {
    companion object {
        // serveur
        private const val BASE_URL = "https://chants.epechassieu.fr/audio/"
    }

fun getAudioUrl(audioFileName: String): String {
    return "$BASE_URL$audioFileName"
}
}