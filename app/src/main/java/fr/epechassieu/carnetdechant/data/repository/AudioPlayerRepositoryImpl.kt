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

@Singleton
class AudioPlayerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioPlayerRepository {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build()

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

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onPlaybackStateChanged(state: Int) {
                _buffering.value = (state == Player.STATE_BUFFERING)
                if (state == Player.STATE_READY) {
                    _duration.value = player.duration.coerceAtLeast(0L)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _error.value = "Erreur lecture: ${error.message}"
            }
        })

        // Mise à jour position
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                if (player.isPlaying) {
                    _currentPosition.value = player.currentPosition
                }
                delay(100)
            }
        }
    }

    override fun prepare(audioUrl: String) {
        val mediaItem = MediaItem.fromUri(audioUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        _error.value = null
    }

    override fun play() = player.play()
    override fun pause() = player.pause()
    override fun stop() {
        player.stop()
        player.seekTo(0)
    }

    override fun seekTo(position: Long) {
        player.seekTo(position)
    }

    override fun release() {
        player.release()
    }
}