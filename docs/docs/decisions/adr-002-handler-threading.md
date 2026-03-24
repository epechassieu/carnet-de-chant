# ADR-002: Handler Threading pour Audio MediaPlayer

## Status
✅ **Accepted** (Résolu bugs de production)

## Context

Après migration vers MP3, l'app crashait lors de la lecture audio sur certains appareils.

### Symptôme d'erreur:
```
E/AndroidRuntime: FATAL EXCEPTION: main
    android.media.MediaPlayer cannot be released during playback
    ...
    at HymnPlayerViewModel.playAudio(HymnPlayerViewModel.kt:45)
```

### Root cause:
- MediaPlayer requiert **main thread** pour manipulation UI
- Jetpack Compose recompose fréquemment
- MediaPlayer instance perdait référence lors recomposition
- Appel MediaPlayer.release() depuis thread worker

## Decision

Utiliser `Handler(Looper.getMainLooper())` pour garantir tous les appels MediaPlayer sur main thread.

### Pattern:

```kotlin
private val mainHandler = Handler(Looper.getMainLooper())

fun playAudio(url: String) {
    // Toujours garantir main thread
    mainHandler.post {
        mediaPlayer?.play()  // Safe ✅
    }
}
```

## Why Not Coroutines withContext(Dispatchers.Main)?

❌ **Mauvais**: Coroutines delays
```kotlin
viewModelScope.launch {
    withContext(Dispatchers.Main) {
        mediaPlayer.start()  // Peut avoir lag
    }
}
```

✅ **Bon**: Handler direct
```kotlin
mainHandler.post {
    mediaPlayer.start()  // Immédiat
}
```

**Raison**: Handler = guaranteed immediate execution sur main thread  
Coroutine = peut être délayée par scheduler

## Architecture impacte

```
┌──────────────────────────────┐
│  ViewModel/Composable        │
└──────────────────────────────┘
        ↓
┌──────────────────────────────┐
│  AudioPlayer.playAudio()     │
└──────────────────────────────┘
        ↓
┌──────────────────────────────┐
│  mainHandler.post { }        │
│  (Thread-safe wrapper)       │
└──────────────────────────────┘
        ↓
┌──────────────────────────────┐
│  MediaPlayer.start()         │
│  (Main thread guaranteed)    │
└──────────────────────────────┘
```

## Implementation complète

```kotlin
class AudioPlayer @Inject constructor(
    context: Context
) {
    private var mediaPlayer: MediaPlayer? = null
    
    // ✅ Pattern clé
    private val mainHandler = Handler(Looper.getMainLooper())
    
    fun playAudio(url: String) {
        mainHandler.post {
            stopAudio() // Nettoyer d'abord
            
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    mp.start()
                }
                prepareAsync()
            }
        }
    }
    
    fun stopAudio() {
        mainHandler.post {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
            mediaPlayer = null
        }
    }
}
```

## Alternatives considered

| Option | Pros | Cons | Verdict |
|--------|------|------|---------|
| `Handler` | ✅ Immédiat, garanti | ⚠️ Ancien API | **RETENU** |
| `withContext(Main)` | ✅ Moderne, coroutines | ❌ Peut délayer | Rejeté |
| `runOnUiThread()` | ✅ Simple | ❌ Activité-dépendant | Rejeté |
| `GlobalScope.launch` | ❌ Anti-pattern | ❌ Memory leak | **Interdit** |

## Tests

```kotlin
@get:Rule
val instantExecutorRule = InstantTaskExecutorRule()

@Test
fun testAudioPlaybackThreadSafety() {
    val audioPlayer = AudioPlayer(context)
    
    // Simule appel depuis background thread
    Thread {
        audioPlayer.playAudio("https://example.com/hymn.mp3")
    }.start()
    
    // Vérifie sur main thread
    assertTrue(audioPlayer.isPlaying())
}
```

## Consequences

### ✅ Benefits
- Pas de race conditions
- MediaPlayer lifecycle géré proprement
- Offline crash résolu

### ⚠️ Risks
- Handler peut être nul si app terminée
- Nécessite context valid

## Follow-up

- [ ] Migration vers `Handler.createAsync()` (API 28+)
- [ ] Monitoring: Logger crash MediaPlayer
- [ ] Performance: Vérifier overhead Handler

---

**Date**: Mars 2026  
**Author**: Equipe Carnet de Chants  
**Related**: [Gestion audio](../architecture/audio.md)
