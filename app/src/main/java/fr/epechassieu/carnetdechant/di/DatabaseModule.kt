package fr.epechassieu.carnetdechant.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.epechassieu.carnetdechant.data.database.AppDatabase
import fr.epechassieu.carnetdechant.data.database.dao.SongDao
import fr.epechassieu.carnetdechant.data.database.dao.UrlMediaUserDao
import javax.inject.Singleton


/**
 * Dagger Hilt module responsible for providing database-related dependencies.
 *
 * This module is installed in the [SingletonComponent], ensuring that the database
 * instance and its DAOs are shared across the entire application lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "carnet_chants_database"
        )
            .build()
    }

    /**
     * Provides the [SongDao] instance to be used for database operations related to songs.
     *
     * @param appDatabase The [AppDatabase] instance from which the DAO is retrieved.
     * @return The [SongDao] used for accessing song data.
     */
    @Provides
    @Singleton
    fun provideSongDao(appDatabase: AppDatabase): SongDao {
        return appDatabase.songDao()
    }

    /**
     * Provides the [UrlMediaUserDao] instance to be used for database operations related to média.
     *
     * @param appDatabase The [AppDatabase] instance from which the DAO is retrieved.
     * @return The [UrlMediaUserDao] used for accessing url data.
     */
    @Provides
    @Singleton
    fun provideUrlMediaUserDao(appDatabase: AppDatabase): UrlMediaUserDao {
        return appDatabase.urlMediaUserDao()
    }
}