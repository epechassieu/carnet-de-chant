package fr.epechassieu.carnetdechant.ui.listen

import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser

data class ListenUiState(
    val officialUrl: String? = null,
    val userUrls: List<UrlMediaUser> = emptyList(),
    val songTitle: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)