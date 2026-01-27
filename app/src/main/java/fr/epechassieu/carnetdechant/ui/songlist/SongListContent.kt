package fr.epechassieu.carnetdechant.ui.songlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song

@Composable
fun SongListContent(
    state: SongListUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSongClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        // 1. Zone de Recherche
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Rechercher un chant...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(24.dp)
        )

        // 2. La Liste (pilotée par l'état)
        Box(modifier = Modifier.fillMaxSize()) {
            when (state) {
                is SongListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SongListUiState.Error -> {
                    Text(
                        text = "Erreur : ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is SongListUiState.Success -> {
                    if (state.songs.isEmpty()) {
                        Text(
                            text = if(searchQuery.isBlank()) "Aucun chant importé." else "Aucun résultat pour cette recherche.",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        LazyColumn {
                            items(state.songs) { song ->
                                SongItem(
                                    song = song,
                                    onClick = { onSongClick(song.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, device = "id:pixel_9")
@Composable
fun SongListContentPreview() {

    val fakeSongs = listOf(
        Song(
            id = "1", songbook = "JEM", number = 100, title = "Dieu est grand", categories = listOf(
                Category.LOUANGE
            ), lyrics = "Test", urlMedia = ""
        ),
        Song(id = "2", songbook = "ATG", number = 42, title = "Jésus t'aime", categories = listOf(Category.ADORATION), lyrics = "Test", urlMedia = "")
    )


    SongListContent(
        state = SongListUiState.Success(fakeSongs),
        searchQuery = "Recherche...",
        onSearchQueryChange = {},
        onSongClick = {}
    )
}