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
    val recueil: String,
    val numero: Int,
    val titre: String,
    val categories: List<String>,
    val paroles: String,
    val urlmedia: String? = null
)