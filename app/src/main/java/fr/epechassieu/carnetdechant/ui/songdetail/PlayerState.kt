package fr.epechassieu.carnetdechant.ui.songdetail

data class PlayerState(
    val isPrepared: Boolean = false,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isBuffering: Boolean = false,
    val error: String? = null,
    val songTitle: String = ""
)
