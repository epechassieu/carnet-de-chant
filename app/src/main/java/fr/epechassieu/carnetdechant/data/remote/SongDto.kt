package fr.epechassieu.carnetdechant.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class SongsResponseDto(
    val version: String,
    val dateGeneration: String,
    val nombreChants: Int,
    val chants: List<SongDto>
)

@Serializable
data class SongDto(
    val id: String,
    val songbook: String,
    val number: Int,
    val title: String,
    val categories: List<String>,
    val lyrics: String,
    val urlMedia: String? = null
)