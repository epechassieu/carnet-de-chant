package fr.epechassieu.carnetdechant.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.epechassieu.carnetdechant.data.repository.SongRepositoryImpl
import fr.epechassieu.carnetdechant.data.repository.UrlMediaUserRepositoryImpl
import fr.epechassieu.carnetdechant.domain.repository.SongRepository
import fr.epechassieu.carnetdechant.domain.repository.UrlMediaUserRepository
import javax.inject.Singleton

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
    abstract fun bindUrlMediaUserRepository(
        urlMediaUserRepositoryImpl: UrlMediaUserRepositoryImpl
    ): UrlMediaUserRepository
}
