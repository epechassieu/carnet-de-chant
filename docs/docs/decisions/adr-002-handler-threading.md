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
    at SongPlayerViewModel.playAudio(SongPlayerViewModel.kt:45)
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
class AudioPlayerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioPlayerRepository {

    private var player: ExoPlayer? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _isPlaying = MutableStateFlow(false)
    private val _currentPosition = MutableStateFlow(0L)
    private val _duration = MutableStateFlow(0L)
    private val _buffering = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    override val isPlaying = _isPlaying.asStateFlow()
    override val currentPosition = _currentPosition.asStateFlow()
    override val duration = _duration.asStateFlow()
    override val buffering = _buffering.asStateFlow()
    override val error = _error.asStateFlow()

    private val positionRunnable = object : Runnable {
        override fun run() {
            player?.let {
                if (it.isPlaying) {
                    _currentPosition.value = it.currentPosition
                }
            }
            mainHandler.postDelayed(this, 100)
        }
    }

    private fun getOrCreatePlayer(): ExoPlayer {
        if (player == null) {
            player = ExoPlayer.Builder(context).build().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        android.util.Log.d("AUDIO", "onIsPlayingChanged: $isPlaying")
                        _isPlaying.value = isPlaying
                    }

                    override fun onPlaybackStateChanged(state: Int) {
                        android.util.Log.d("AUDIO", "onPlaybackStateChanged: $state")
                        _buffering.value = (state == Player.STATE_BUFFERING)
                        if (state == Player.STATE_READY) {
                            _duration.value = duration.coerceAtLeast(0L)
                            android.util.Log.d("AUDIO", "Duration set to: ${_duration.value}")
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        android.util.Log.e("AUDIO", "Player error: ${error.message}")
                        _error.value = "Erreur lecture: ${error.message}"
                    }
                })
            }
            mainHandler.post(positionRunnable)
        }
        return player!!
    }

    override fun prepare(audioUrl: String) {
        android.util.Log.d("AUDIO", "prepare() called with URL: $audioUrl")

        _currentPosition.value = 0L
        _duration.value = 0L
        _isPlaying.value = false
        _buffering.value = false
        _error.value = null

        val p = getOrCreatePlayer()
        p.clearMediaItems()
        p.setMediaItem(MediaItem.fromUri(audioUrl))
        p.prepare()

        android.util.Log.d("AUDIO", "Player state after prepare: ${p.playbackState}")
    }

    override fun play() {
        player?.play()
    }

    override fun pause() {
        player?.pause()
    }

    override fun stop() {
        player?.let {
            it.pause()
            it.seekTo(0)
        }
        _currentPosition.value = 0L
        _isPlaying.value = false
    }

    override fun seekTo(position: Long) {
        player?.seekTo(position)
    }

    override fun release() {
        mainHandler.removeCallbacks(positionRunnable)
        player?.release()
        player = null
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
