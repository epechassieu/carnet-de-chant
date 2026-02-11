package fr.epechassieu.carnetdechant.ui.songdetail

import android.R.attr.text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import fr.epechassieu.carnetdechant.R
import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.ui.theme.CarnetDeChantTheme


/**
 * Display the detail screen of a specific song.
 *
 * This composable connects the [SongDetailViewModel] to the [SongDetailContent] UI,
 * handling the state collection and providing navigation callbacks.
 *
 * @param onBackClick Callback invoked when the user presses the back navigation button.
 * @param onListenClick Callback invoked when the user wants to listen to or add an audio recording,
 * passing the song's unique identifier as a parameter.
 * @param viewModel The ViewModel that manages the state of the song detail,
 * injected by default via Hilt.
 */
@Composable
fun SongDetailScreen(
    onBackClick: () -> Unit,
    onListenClick: (String) -> Unit = {},
    viewModel: SongDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SongDetailContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onListenClick = {
            if (uiState is SongDetailUiState.Success) {
                onListenClick((uiState as SongDetailUiState.Success).song.id)
            }
        }
    )
}

/**
 * Composable that displays the layout for the song detail screen.
 *
 * This function handles the visual representation of the song's information, including
 * the top bar with the song title, the metadata (songbook, number, categories),
 * action buttons for media/recordings, and the song lyrics. It reacts to different
 * [SongDetailUiState] states (Loading, Error, Success).
 *
 * @param modifier The [Modifier] to be applied to the layout.
 * @param uiState The current state of the UI, providing the song data or error/loading status.
 * @param onBackClick Callback invoked when the navigation back button is clicked.
 * @param onListenClick Callback invoked when the user clicks the button to manage audio recordings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailContent(
    modifier: Modifier = Modifier,
    uiState: SongDetailUiState,
    onBackClick: () -> Unit,
    onListenClick: () -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (uiState) {
                            is SongDetailUiState.Success -> uiState.song.title
                            else -> stringResource(R.string.song_detail_loading)
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.song_backclic)
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is SongDetailUiState.Loading -> CircularProgressIndicator(
                    Modifier.align(Alignment.Center)
                )

                is SongDetailUiState.Error -> Text(
                    text = uiState.message,
                    Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )

                is SongDetailUiState.Success -> {
                    val song = state.song

                    // --- lyrics ---
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(start=16.dp, end=16.dp, top=16.dp, bottom=64.dp)
                            .verticalScroll(rememberScrollState())
                    ) {

                        // -- infos song --
                        Text(
                            text = "${song.songbook} n°${song.number} | ${song.categories.joinToString { it.libelle }}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // -- buttons --

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // - offical url -
                            if (!song.urlMedia.isNullOrBlank()) {
                                Button(
                                    onClick = {
                                        try {
                                            uriHandler.openUri(song.urlMedia)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    },
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(text = stringResource(R.string.song_detail_button_text))
                                }
                            }
                            // - add url -
                            OutlinedButton(
                                onClick = onListenClick,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(text = stringResource(R.string.song_detail_add_audio))
                            }
                        }

                        // -- lyrics --
                        Text(
                            text = song.lyrics,
                            modifier = Modifier.padding(top = 24.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5, // Plus aéré
                            textAlign = TextAlign.Start // Ou Start selon les goûts
                        )

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_9")
@Composable
fun SongDetailContentPreview() {
    val fakeSong = Song(
        id = "1",
        songbook = "JEM",
        number = 100,
        title = "Dieu est grand",
        categories = listOf(Category.LOUANGE),
        lyrics = "Couplet 1\nDieu est grand...\n\n[Refrain]\nAlléluia...",
        urlMedia = "https://youtube.com/watch?v=123"
    )

    CarnetDeChantTheme {
        SongDetailContent(
            uiState = SongDetailUiState.Success(fakeSong),
            onBackClick = {},
            onListenClick = {}
        )
    }
}