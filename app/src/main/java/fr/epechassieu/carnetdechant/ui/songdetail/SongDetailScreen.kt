package fr.epechassieu.carnetdechant.ui.songdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.epechassieu.carnetdechant.R
import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.domain.model.TextSize
import fr.epechassieu.carnetdechant.ui.settings.TextSizeMenu
import fr.epechassieu.carnetdechant.ui.theme.CarnetDeChantTheme
import fr.epechassieu.carnetdechant.ui.songdetail.PlayerState
import fr.epechassieu.carnetdechant.ui.songdetail.SongDetailUiState
import fr.epechassieu.carnetdechant.ui.songdetail.SongDetailViewModel


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
    songId: String,
    onBackClick: () -> Unit,
    textSize: TextSize = TextSize.Default,
    onTextSizeChange: (TextSize) -> Unit = {},
    viewModel: SongDetailViewModel = hiltViewModel()
) {

    LaunchedEffect(songId) {
        viewModel.loadSong(songId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()

    SongDetailContent(
        uiState = uiState,
        playerState = playerState,  // Passer le state
        onBackClick = onBackClick,
        textSize = textSize,
        onTextSizeChange = onTextSizeChange,
        onPrepareAudio = { song -> viewModel.prepareAudio(song) },
        onTogglePlayPause = { viewModel.togglePlayPause() },
        onSeek = { viewModel.seekTo(it) },
        onStop = { viewModel.stopAudio() }
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
    playerState: PlayerState,
    onBackClick: () -> Unit,
    textSize: TextSize = TextSize.Default,
    onTextSizeChange: (TextSize) -> Unit = {},
    onPrepareAudio: (Song) -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onStop: () -> Unit
) {
// État du BottomSheet
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // Commence directement ouvert à 100%
    )
    var showBottomSheet by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    //Text("Détail du chant")
                    Text(
                        text = when (uiState) {
                            is SongDetailUiState.Success -> uiState.song.title
                            else -> "détail du chant"
                        },
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                        },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                actions = {
                    TextSizeMenu(
                        currentTextSize = textSize,
                        onTextSizeChange = onTextSizeChange
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            // Mini player toujours visible en bas
            if (playerState.isPrepared) {
                MiniPlayer(
                    isPlaying = playerState.isPlaying,
                    title = playerState.songTitle,
                    onPlayPause = { onTogglePlayPause() },
                    onExpand = { showControls = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is SongDetailUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is SongDetailUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    modifier = Modifier.padding(padding)
                )
            }

            is SongDetailUiState.Success -> {
                val song = state.song

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // En-tête du chant
                    SongHeader(song = song)

                    // Bouton Écouter
                    ListenButton(
                        onClick = {
                            onPrepareAudio(song)
                            showControls = true
                        },
                        isLoading = playerState.isBuffering,
                        enabled = song.audioUrl != null
                    )

                    if (song.audioUrl == null) {
                        Text(
                            text = "Audio non disponible",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Paroles (toujours visibles)
                    LyricsSection(
                        lyrics = song.lyrics,
                    )

                    // Espace pour le player
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

// BottomSheet avec contrôles
    if (showControls) {
        ModalBottomSheet(
            onDismissRequest = { showControls = false },
            sheetState = sheetState
        ) {
            FullPlayerControls(
                state = playerState,
                onPlayPause = { onTogglePlayPause() },
                onSeek = { onSeek(it) },
                onStop = {
                    onStop()
                    showControls = false
                }
            )
        }
    }
}

// Mini player compact (toujours visible)
@Composable
private fun MiniPlayer(
    isPlaying: Boolean,
    title: String,
    onPlayPause: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 8.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Lecture"
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = "Lecture en cours",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }

            IconButton(onClick = onExpand) {
                Icon(Icons.Default.ExpandLess, "Agrandir")
            }
        }
    }
}

// Contrôles complets (BottomSheet)
@Composable
private fun FullPlayerControls(
    state: PlayerState,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = state.songTitle,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Barre de progression
        Slider(
            value = state.currentPosition.toFloat(),
            onValueChange = { onSeek(it.toLong()) },
            valueRange = 0f..state.duration.toFloat().coerceAtLeast(1f),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(state.currentPosition))
            Text(formatTime(state.duration))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Boutons principaux
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Recul 10s
            IconButton(onClick = { onSeek((state.currentPosition - 10000).coerceAtLeast(0)) }) {
                Icon(Icons.Default.Replay10, "Recul 10s")
            }

            // Play/Pause (bouton principal)
            FilledIconButton(
                onClick = onPlayPause,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Avance 10s
            IconButton(onClick = { onSeek((state.currentPosition + 10000).coerceAtMost(state.duration)) }) {
                Icon(Icons.Default.Forward10, "Avance 10s")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stop
        OutlinedButton(
            onClick = onStop,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(Icons.Default.Stop, null)
            Spacer(Modifier.width(8.dp))
            Text("Arrêter")
        }

        if (state.isBuffering) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}

private fun formatTime(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = (ms / 1000 / 60)
    return "%d:%02d".format(minutes, seconds)
}

@Composable
private fun SongHeader(song: Song) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // -- infos song --
        Text(
            text = "${song.songbook} n°${song.number} | ${song.categories.joinToString { it.libelle }}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ListenButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    enabled: Boolean
) {
    val containerColor = if (enabled) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Icon(
            imageVector = Icons.Default.PlayCircle,
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = if (enabled) "Écouter le chant" else "Aucun lien audio disponible",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun LyricsSection(
    lyrics: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Paroles",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        ElevatedCard(
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 2.dp
            ),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Text(
                text = lyrics,
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = MaterialTheme.typography.bodyLarge.fontSize * 1.6
                ),
                modifier = Modifier.padding(20.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
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
        audio = "JEM100.MP3"
    )

    CarnetDeChantTheme {
        SongDetailContent(
            uiState = SongDetailUiState.Success(fakeSong),
            playerState = PlayerState(),  // State vide pour la preview
            onBackClick = {},
            onPrepareAudio = {},
            onTogglePlayPause = {},
            onSeek = {},
            onStop = {}
        )
    }
}