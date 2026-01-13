package fr.epechassieu.carnetdechant.domain.model

data class UrlMedia(
    val id: Long = 0,
    val chantId: String,
    val url: String,
    val type: CategoryMedia,
    val description: String? = null
)
