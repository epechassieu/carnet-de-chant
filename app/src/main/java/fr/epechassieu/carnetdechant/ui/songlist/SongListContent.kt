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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.epechassieu.carnetdechant.R
import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song

@Composable
fun SongListContent(
    modifier : Modifier = Modifier,
    state: SongListUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSongClick: (String) -> Unit
) {
    Column(modifier.fillMaxSize()) {

        // 1. Zone de Recherche
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text(text=stringResource(R.string.song_listcontent_outlinedeextfield_placeholder)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.song_listcontent_clear_button)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp)
        )

        // 2. La Liste (pilotée par l'état)
        Box(modifier.fillMaxSize()) {
            when (state) {
                is SongListUiState.Loading -> {
                    CircularProgressIndicator(modifier.align(Alignment.Center))
                }
                is SongListUiState.Error -> {
                    Text(
                        text = stringResource(R.string.song_listcontent_error_message, state.message),
                        modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is SongListUiState.Success -> {
                    if (state.songs.isEmpty()) {

                        val emptyMessage = if (searchQuery.isBlank()) {
                            stringResource(R.string.song_listcontent_empty_database)
                        }  else {
                            stringResource(R.string.song_list_no_search_results)
                        }

                        Text(
                            text = emptyMessage,
                            modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )

                    } else {
                        LazyColumn {
                            items(
                                items =state.songs,
                                key = {it.id} // pour éviter de tout recomposer quand la liste change id et pas position
                            ) { song ->
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