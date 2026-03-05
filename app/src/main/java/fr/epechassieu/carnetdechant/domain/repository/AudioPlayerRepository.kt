package fr.epechassieu.carnetdechant.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerRepository {
    fun prepare(audioUrl: String)
    fun play()
    fun pause()
    fun stop()
    fun seekTo(position: Long)
    fun release()

    val isPlaying: StateFlow<Boolean>
    val currentPosition: StateFlow<Long>
    val duration: StateFlow<Long>
    val buffering: StateFlow<Boolean>
    val error: StateFlow<String?>
}