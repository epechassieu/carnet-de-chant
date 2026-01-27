package fr.epechassieu.carnetdechant.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.http.ContentType
import kotlinx.serialization.json.Json //configure parsing Json
import javax.inject.Singleton

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
}