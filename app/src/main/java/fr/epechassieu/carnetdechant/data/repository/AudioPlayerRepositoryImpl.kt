package fr.epechassieu.carnetdechant.data.repository

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.epechassieu.carnetdechant.domain.repository.AudioPlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import android.os.Handler
import android.os.Looper

@Singleton
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