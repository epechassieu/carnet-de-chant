package fr.epechassieu.carnetdechant.domain.model

data class Song(
    val id: String,
    val recueil: String,
    val numero: Int,
    val titre: String,
    val categories: List<Category>,
    val paroles: String
)
