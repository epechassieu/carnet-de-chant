# Clean Architecture

## Principes appliqués

Carnet de Chants suit les principes de **Clean Architecture** pour maximiser:
- 🔄 La testabilité
- 🔧 La maintenabilité
- 🎯 L'indépendance des frameworks

## Les 3 couches

### 1. **Presentation Layer** (UI)
Responsable de l'affichage et de l'interaction utilisateur.

**Responsabilités**:
- Afficher l'état
- Capturer les événements utilisateur
- Déléguer au ViewModel

### 2. **Domain Layer** (Business Logic)
Contient la logique métier, **indépendante de toute technologie**.


**Responsabilités**:
- Définir entités
- Implémenter règles métier
- Zéro dépendance Android/Hilt

### 3. **Data Layer** (Persistence & Networking)
Gère l'accès aux données (Room, Ktor, SharedPreferences).


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
        dao: SongDao,
        apiClient: ApiClient
    ): SongRepository = SongRepositoryImpl(dao, apiClient)
}

@HiltViewModel
class SongListViewModel @Inject constructor(
    private val getAllSongsUseCase: GetAllsongsUseCase
) : ViewModel() {
    // ...
}
```

## Avantages observés

| Avantage | Exemple Carnet de Chants                    |
|----------|---------------------------------------------|
| **Testabilité** | Tests unitaires SongRepository sans Room    |
| **Maintenabilité** | Changement YouTube → MP3 isolé à Data layer |
| **Réutilisabilité** | UseCase peut servir UI Compose ET CLI       |
| **Indépendance** | Domain layer = 0 dépendance Android         |

## Anti-patterns à éviter

❌ Logique métier dans les Composables
❌ Dépendances Android dans Domain layer
❌ ViewModels qui accèdent directement à Room
❌ Spaghetti code sans couches claires

---

**Pour plus**: Voir [ADR-001: YouTube → MP3](../decisions/adr-001-youtube-to-mp3.md)
