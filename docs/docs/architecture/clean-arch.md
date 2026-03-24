# Clean Architecture

## Principes appliqués

Carnet de Chants suit les principes de **Clean Architecture** proposés par Robert C. Martin pour maximiser:
- 🔄 La testabilité
- 🔧 La maintenabilité
- 🎯 L'indépendance des frameworks

## Les 3 couches

### 1. **Presentation Layer** (UI)
Responsable de l'affichage et de l'interaction utilisateur.

```kotlin
// Exemples Carnet de Chants
@Composable
fun HymnListScreen(viewModel: HymnListViewModel) {
    val state by viewModel.uiState.collectAsState()
    
    Column {
        TextField(
            value = state.searchQuery,
            onValueChange = { viewModel.updateSearch(it) }
        )
        LazyColumn {
            items(state.hymns) { hymn ->
                HymnCard(hymn)
            }
        }
    }
}
```

**Responsabilités**:
- Afficher l'état
- Capturer les événements utilisateur
- Déléguer au ViewModel

### 2. **Domain Layer** (Business Logic)
Contient la logique métier, **indépendante de toute technologie**.

```kotlin
// Entités métier
data class Hymn(
    val id: Int,
    val title: String,
    val audioUrl: String,
    val dateAdded: Long
)

// Use cases
class GetAllHymnsUseCase(
    private val repository: HymnRepository
) {
    suspend operator fun invoke(): List<Hymn> {
        return repository.getAllHymns()
    }
}
```

**Responsabilités**:
- Définir entités
- Implémenter règles métier
- Zéro dépendance Android/Hilt

### 3. **Data Layer** (Persistence & Networking)
Gère l'accès aux données (Room, Ktor, SharedPreferences).

```kotlin
// Repository implémentation
class HymnRepositoryImpl(
    private val dao: HymnDao,
    private val apiClient: ApiClient
) : HymnRepository {
    override suspend fun getAllHymns(): List<Hymn> {
        return dao.getAll()
    }
    
    override suspend fun searchHymns(query: String): List<Hymn> {
        return dao.searchByTitle("%$query%")
    }
}
```

**Responsabilités**:
- Abstraire les sources de données
- Mapper API ↔ Entities
- Gérer cache/sync

## Flux de données

```
User Action
    ↓
Composable (UI)
    ↓
ViewModel (StateManagement)
    ↓
UseCase (Business Logic)
    ↓
Repository (Data Abstraction)
    ├→ Room DAO (Local)
    └→ Ktor Client (Remote)
    ↓
Database / API
```

## Dépendances et Hilt

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideHymnRepository(
        dao: HymnDao,
        apiClient: ApiClient
    ): HymnRepository = HymnRepositoryImpl(dao, apiClient)
}

@HiltViewModel
class HymnListViewModel @Inject constructor(
    private val getAllHymnsUseCase: GetAllHymnsUseCase
) : ViewModel() {
    // ...
}
```

## Avantages observés

| Avantage | Exemple Carnet de Chants |
|----------|-------------------------|
| **Testabilité** | Tests unitaires HymnRepository sans Room |
| **Maintenabilité** | Changement YouTube → MP3 isolé à Data layer |
| **Réutilisabilité** | UseCase peut servir UI Compose ET CLI |
| **Indépendance** | Domain layer = 0 dépendance Android |

## Anti-patterns à éviter

❌ Logique métier dans les Composables
❌ Dépendances Android dans Domain layer
❌ ViewModels qui accèdent directement à Room
❌ Spaghetti code sans couches claires

---

**Pour plus**: Voir [ADR-001: YouTube → MP3](../decisions/adr-001-youtube-to-mp3.md)
