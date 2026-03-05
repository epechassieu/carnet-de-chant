package fr.epechassieu.carnetdechant.ui.songlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.epechassieu.carnetdechant.R
import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.ui.theme.CarnetDeChantTheme

/**
 * A Composable that displays the main content of the song list screen.
 * It includes a search bar and a list of songs, handling different UI states
 * such as loading, error, empty, and success.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param state The current state of the song list UI (e.g., Loading, Success, Error).
 * @param searchQuery The current text in the search input field.
 * @param onSearchQueryChange A callback invoked when the search query text changes.
 * @param onSongClick A callback invoked when a song item is clicked, passing the song's ID.
 */

@Composable
fun SongListContent(
    modifier: Modifier = Modifier,
    state: SongListUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSongClick: (String) -> Unit
) {
    Column(modifier.fillMaxSize()) {

        // -- query search bar--

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text(text = stringResource(R.string.song_listcontent_outlinedeextfield_placeholder)) },
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

        // -- items --
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is SongListUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is SongListUiState.Error -> {
                    Text(
                        text = stringResource(
                            R.string.song_listcontent_error_message,
                            state.message
                        ),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is SongListUiState.Success -> {
                    if (state.songs.isEmpty()) {

                        val emptyMessage = if (searchQuery.isBlank()) {
                            stringResource(R.string.song_listcontent_empty_database)
                        } else {
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
                                items = state.songs,
                                key = { it.id } // compose list with id and not position
                            ) { song ->
                                SongItem(
                                    song = song,
                                    onClick = { onSongClick(song.id) },
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
            ), lyrics = "Test", audio = "JEM100.mp3"
        ),
        Song(
            id = "2",
            songbook = "ATG",
            number = 42,
            title = "Jésus t'aime",
            categories = listOf(Category.ADORATION),
            lyrics = "Test",
            audio = "ATG42.mp3"
        )
    )

    CarnetDeChantTheme {
        SongListContent(
            state = SongListUiState.Success(fakeSongs),
            searchQuery = "",
            onSearchQueryChange = {},
            onSongClick = {}
        )
    }
}