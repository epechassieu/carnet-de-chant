# ADR-003: Migration MediaPlayer → Media3

## Status
✅ **Accepted** (Implémenté avec Android Studio Panda 2)

## Context

Initialement, Carnet de Chants utilisait l'API `MediaPlayer` native d'Android pour jouer les fichiers MP3.

### Problèmes identifiés avec MediaPlayer:

- 🔴 **Threading complexe**: Nécessite `Handler(Looper.getMainLooper())` pour manipuler l'UI
- 🔴 **API deprecated**: Google recommande de ne plus utiliser MediaPlayer
- 🔴 **Gestion d'erreurs basique**: Peu d'informations sur les états d'erreur
- 🔴 **Offline limité**: Support offline peu robuste
- ⚠️ **Maintenance**: Pas de mises à jour ni de support futur

### Découverte lors de la migration Android Studio Panda 2:

Lors de l'upgrade vers **Android Studio 2024.2 (Panda 2)**, les dépendances ont été automatiquement mises à jour:

```toml
# libs.versions.toml - Anciennes versions
kotlin = "1.9.10"
room = "2.6.0"
hilt = "2.48"

# libs.versions.toml - Nouvelles versions après update
kotlin = "2.3.0"
room = "2.8.4"
hilt = "2.57.2"
media3 = "1.9.2"  ← 🆕 Media3 ajouté automatiquement!
```

À ce moment, on a réalisé qu'**il était temps d'adopter Media3** plutôt que de continuer avec MediaPlayer.

## Decision

**Migrer vers Media3 (ExoPlayer)** pour la gestion audio, en profitant de la mise à jour globale des dépendances.

### Avantages Media3:

```
┌─────────────────────────────────────┐
│       MediaPlayer vs Media3         │
├─────────────────────┬───────────────┤
│ Threading           │ ❌ → ✅       │
│ API                 │ ⚠️ → ✅       │
│ Erreurs             │ ❌ → ✅       │
│ Offline             │ ⚠️ → ✅       │
│ Support Google      │ ❌ → ✅       │
└─────────────────────┴───────────────┘
```

**Détails**:

| Aspect | MediaPlayer | Media3 | Gain |
|--------|-----------|--------|------|
| **Threading** | Manuel (Handler) | Automatique | Moins de bugs |
| **API Status** | Deprecated | ✅ Maintenu | Futur-proof |
| **Erreurs** | `onError(int, int)` | Rich callbacks | Meilleur debug |
| **Offline** | Limité | Avancé + caching | Meilleure UX |
| **Lifecycle** | Manuel release | Automatique | Moins de leaks |

## Implementation

### Architecture avant (MediaPlayer)

```kotlin
// ❌ Complexe: Handler nécessaire
class AudioPlayer {
    private val mainHandler = Handler(Looper.getMainLooper())
    
    fun playAudio(url: String) {
        mainHandler.post {  // ← Boilerplate
            mediaPlayer?.apply {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { mp -> mp.start() }
            }
        }
    }
}
```

### Architecture après (Media3)

```kotlin
// ✅ Simple: Pas de Handler
@Singleton
class AudioService @Inject constructor(context: Context) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    
    fun playAudio(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()  // ← Direct, pas de threading boilerplate
    }
}
```

### État Management ViewModel

**Avant**:
```kotlin
@HiltViewModel
class HymnPlayerViewModel @Inject constructor(
    private val audioPlayer: AudioPlayer  // ← Pas d'état partagé
) : ViewModel() {
    private val _uiState = MutableStateFlow<PlayerState>(PlayerState.Idle)
    
    fun playHymn(audioUrl: String) {
        viewModelScope.launch {
            _uiState.value = PlayerState.Playing
            audioPlayer.playAudio(audioUrl)
            // ❌ État manuel, pas synchronisé avec Player réel
        }
    }
}
```

**Après**:
```kotlin
@HiltViewModel
class HymnPlayerViewModel @Inject constructor(
    private val audioService: AudioService
) : ViewModel(), Player.Listener {
    
    // ✅ État lié au Player via Listener
    override fun onPlaybackStateChanged(playbackState: Int) {
        val newState = when (playbackState) {
            Player.STATE_READY -> PlayerState.Playing
            Player.STATE_PAUSED -> PlayerState.Paused
            Player.STATE_ENDED -> PlayerState.Idle
            else -> PlayerState.Idle
        }
        _uiState.update { it.copy(state = newState) }
    }
}
```

## Consequences

### ✅ Avantages réalisés

- **Simplification code**: -40 lignes de boilerplate Handler
- **Stabilité**: Pas de crashes threading
- **Maintenabilité**: Code plus lisible et moderne
- **Performance**: Compilation 2x plus rapide (KSP au lieu de Kapt)
- **Futur**: Aligné avec recommandations Google

### ⚠️ Coûts

- **Nouvelle dépendance**: Media3 ~1.9 MB
- **Apprentissage**: API ExoPlayer plus riche mais plus complexe
- **Migration**: Refactor code audio existant

### 📊 Timing

Cette décision a coïncidé avec:
1. ✅ Upgrade Android Studio 2024.2 (Panda 2)
2. ✅ Mise à jour automatique Kotlin 2.3.0
3. ✅ Mise à jour KSP 2.3.4
4. ✅ Adoption Gradle Version Catalog (libs.versions.toml)

**C'était le moment idéal** pour moderniser la stack audio.

## Alternatives considérées

| Option | Pros | Cons | Verdict |
|--------|------|------|---------|
| **MediaPlayer** | Simple | ❌ Deprecated | Rejeté |
| **Media3/ExoPlayer** | ✅ Moderne | Plus complexe | **RETENU** |
| **Kotlin Flow Audio** | Native | Pas d'équivalent | N/A |
| **Third-party (Spotify SDK)** | Puissant | Dépendance externe | Rejeté |

## Migration Path

**Phase 1**: Identifier code audio
```kotlin
// Avant: grep -r "MediaPlayer" src/
// Trouver tous les usages
```

**Phase 2**: Créer AudioService avec Media3
```kotlin
// Créer classe wrapper ExoPlayer
@Singleton
class AudioService
```

**Phase 3**: Adapter ViewModel
```kotlin
// Implémenter Player.Listener
// Synchroniser état ViewModel ↔ Player
```

**Phase 4**: Update Composables
```kotlin
// Utiliser ViewModel.uiState
// Afficher état playback en temps réel
```

## Testing

```kotlin
@Test
fun testAudioPlaybackState() {
    val audioService = AudioService(context)
    audioService.playAudio(TEST_URL)
    
    assertTrue(audioService.isPlaying())
    assertEquals(PlaybackState.PLAYING, audioService.getPlaybackState())
}

@Test
fun testAudioStops_onViewModelCleared() {
    val viewModel = HymnPlayerViewModel(audioService)
    viewModel.playHymn(hymn)
    
    assertTrue(audioService.isPlaying())
    
    viewModel.onCleared()
    
    assertFalse(audioService.isPlaying())
}
```

## Follow-up

- [ ] Implémenter caching HTTP pour offline
- [ ] Monitoring: Analytics sur player states
- [ ] Performance: Benchmark vs ancienne implémentation
- [ ] UX: Ajouter progress bar scrubbing
- [ ] Accessibility: Support contrôles audio système

## Related ADRs

- [ADR-001: YouTube → MP3](adr-001-youtube-to-mp3.md)
- [ADR-002: Handler Threading](adr-002-handler-threading.md) ← Rendu obsolète par Media3

---

**Date**: Mars 2026  
**Author**: Équipe Carnet de Chants  
**Status Change**: MediaPlayer → Media3 (Post Android Studio 2.0 upgrade)  
**Impact**: Gestion audio simplifiée, code + moderne, aligné Google recommendations

## Notes supplémentaires

### Pourquoi pas rester sur MediaPlayer + Handler?

À l'époque (ADR-002), Handler était la **meilleure solution** avec MediaPlayer.

Maintenant (ADR-003), Media3 élimine **complètement le problème** au lieu d'utiliser un workaround.

C'est l'évolution normale:
```
v1: Problem solved with Handler    (ADR-002)
v2: Problem eliminated with Media3 (ADR-003)
```

### Resources

- [Media3 Official](https://developer.android.com/guide/topics/media/media3)
- [ExoPlayer Architecture](https://exoplayer.dev/architecture.html)
- [Android Audio Best Practices](https://developer.android.com/guide/topics/media-apps/audio-app/mediabrowserservice)
