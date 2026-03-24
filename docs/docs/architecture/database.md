# Base de données - Room

## Schéma entités

### song (Cantique)

```kotlin
@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id:String,

    @ColumnInfo(name = "recueil")
    val songbook: String,

    @ColumnInfo(name = "numero")
    val number: Int,

    @ColumnInfo(name = "titre")
    val title: String,

    @ColumnInfo(name = "categories")
    val categories: String,

    @ColumnInfo(name = "paroles")
    val lyrics: String,

    @ColumnInfo(name = "audio")
    val audio: String?
)
```

### ERD (Entity Relationship Diagram)

```
┌─────────────────────────┐
│       songs             │
├─────────────────────────┤
│ id (PK)                 │
│ title                   │
│ songbook                │
│ number                  │
│ categories              │
│ lyrics                  │
│ audio                   │
└─────────────────────────┘
```

## DAOs (Data Access Objects)

```kotlin
@Dao
interface SongDao {

    // ascending ranking

    @Query("SELECT * FROM songs ORDER BY titre ASC")
    fun getSongsByTitle(): Flow<List<SongEntity>>

    // search by criterias

    @Query("SELECT * FROM songs WHERE categories LIKE '%' || :category || '%'")
    fun getSongsByCategory(category: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id = :id")
    fun getSongById(id: String): Flow<SongEntity?>

    // Json Update

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<SongEntity>)

    // count

    @Query("SELECT COUNT(*) FROM songs")
    suspend fun getSongsCount(): Int

    // deleting
    @Query("DELETE FROM songs")
    suspend fun deleteAll()

    // atomic update
    @Transaction
    suspend fun replaceAllSongs(songs: List<SongEntity>) {
        deleteAll()
        insertAll(songs)
    }
}
```

## Database Configuration

```kotlin
@Database(
    entities = [SongEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao

}
```

## Hilt Binding

```kotlin
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

    @Provides
    @Singleton
    fun provideSongDao(appDatabase: AppDatabase): SongDao {
        return appDatabase.songDao()
    }


}
```

## Migration Future

Si vous devez ajouter des colonnes (ex: `language`):

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE hymns ADD COLUMN language TEXT DEFAULT 'FR'")
    }
}

// Dans Database:
Room.databaseBuilder(context, AppDatabase::class.java, "carnet_chants_database")
    .addMigrations(MIGRATION_1_2)
    .build()
```

## Données de seed

Chargement des 268 chants et mp3 depuis JSON au premier lancement:

```kotlin
class SongRepositoryImpl @Inject constructor(
    private val songDao: SongDao,
    private val songApiService: SongApiService,
    private val audioApiService: AudioApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SongRepository {

    override fun getSongsByTitle(): Flow<List<Song>> {
        return songDao.getSongsByTitle().map { entities ->
            entities.map { it.toDomain(audioApiService) }
        }
    }

    override fun getSongsByCategory(category: Category): Flow<List<Song>> {
        return songDao.getSongsByCategory(category.name).map { entities ->
            entities.map { it.toDomain(audioApiService) }
        }
    }

    override fun getSongById(id: String): Flow<Song?> {
        return songDao.getSongById(id).map { entity ->
            entity?.toDomain(audioApiService)
        }
    }

    override suspend fun isDatabaseEmpty(): Boolean {
        return songDao.getSongsCount() == 0
    }

    /**
     * Synchronizes the local database by fetching songs from the remote API.
     *
     * This function performs the following steps:
     * 1. Downloads song data from the remote service.
     * 2. Maps the response data to local database entities.
     * 3. Persists the entities into the local Room database.
     *
     * It handles various error scenarios including network connectivity issues,
     * API errors, and database transaction failures, returning a localized
     * error message wrapped in a [Result].
     *
     * @return A [Result] containing a success message with the number of imported songs,
     * or a failure containing an exception with a user-friendly error message.
     */
    override suspend fun loadSongsFromJson(): Result<Int> {
        return try {
            val response = songApiService.getSongs()

            if (response.chants.isEmpty()) {
                return Result.failure(AppException.FileNotFound())
            }

            val entities = response.chants.map { it.toEntity() }
            songDao.replaceAllSongs(entities)  // use atomic update


            Result.success(entities.size)

        } catch (e: Exception) {
            val appException = when (e) {
                is UnknownHostException -> AppException.NetworkError()
                is JsonConvertException -> AppException.FileNotFound()
                is SerializationException -> AppException.FileCorrupt()
                is ClientRequestException -> AppException.HttpClientError(e.response.status.value)
                is ServerResponseException -> AppException.ServerError(e.response.status.value)
                is SQLException -> AppException.DatabaseError()
                else -> AppException.Unknown(e.message)
            }
            Result.failure(appException)
        }
    }

}
```

---

**Voir aussi**: [Gestion audio](audio.md)
