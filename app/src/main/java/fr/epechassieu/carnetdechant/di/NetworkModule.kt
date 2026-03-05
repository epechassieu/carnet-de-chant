package fr.epechassieu.carnetdechant.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.epechassieu.carnetdechant.data.remote.AudioApiService
import fr.epechassieu.carnetdechant.data.remote.SongApiService
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.http.ContentType
import kotlinx.serialization.json.Json //configure parsing Json
import javax.inject.Singleton

/**
 * Dagger Hilt module responsible for providing network-related dependencies.
 *
 * This module provides a singleton instance of [HttpClient] configured with the Android engine
 * and content negotiation using Kotlinx Serialization to handle JSON responses.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                val jsonConfig =Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                    prettyPrint = true
                    encodeDefaults = true
                }
                register(ContentType.Text.Plain, KotlinxSerializationConverter(jsonConfig))
                register(ContentType.Application.Json, KotlinxSerializationConverter(jsonConfig))
            }
        }
    }
    @Provides
    @Singleton
    fun provideSongApiService(httpClient: HttpClient): SongApiService {
        return SongApiService(httpClient)
    }

    @Provides
    @Singleton
    fun provideAudioApiService(httpClient: HttpClient): AudioApiService {
        return AudioApiService(httpClient)
    }
}