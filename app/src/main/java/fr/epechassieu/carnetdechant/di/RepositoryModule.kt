package fr.epechassieu.carnetdechant.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.epechassieu.carnetdechant.data.repository.AudioPlayerRepositoryImpl
import fr.epechassieu.carnetdechant.data.repository.SettingsRepositoryImpl
import fr.epechassieu.carnetdechant.data.repository.SongRepositoryImpl
import fr.epechassieu.carnetdechant.domain.repository.AudioPlayerRepository
import fr.epechassieu.carnetdechant.domain.repository.SettingsRepository
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import javax.inject.Singleton

/**
 * Dagger Hilt module responsible for providing repository implementations.
 * This module binds repository interfaces to their concrete implementations
 * and ensures they are provided as singletons throughout the application.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSongRepository(
        songRepositoryImpl: SongRepositoryImpl
    ): SongRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

}

@Module
@InstallIn(SingletonComponent::class)
abstract class AudioModule {

    @Binds
    @Singleton
    abstract fun bindAudioPlayerRepository(
        impl: AudioPlayerRepositoryImpl
    ): AudioPlayerRepository
}
