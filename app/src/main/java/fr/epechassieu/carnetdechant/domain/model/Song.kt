package fr.epechassieu.carnetdechant.domain.model

data class Song(
    val id: String,
    val songbook: String,
    val number: Int,
    val title: String,
    val categories: List<Category>,
    val lyrics: String,
    val urlMedia: String? = null
)
