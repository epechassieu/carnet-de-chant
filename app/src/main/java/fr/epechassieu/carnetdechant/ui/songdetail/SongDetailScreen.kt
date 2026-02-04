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
            modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is SongDetailUiState.Loading -> CircularProgressIndicator(
                    modifier.align(Alignment.Center)
                )

                is SongDetailUiState.Error -> Text(
                    text = stringResource(R.string.song_detail_error),
                    modifier.align(Alignment.Center)
                )

                is SongDetailUiState.Success -> {
                    val song = state.song

                    // --- LE CONTENU DU CHANT ---
                    Column(
                        modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()) // ,Permet de scroller
                        //horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Recueil, numero et categories
                        Text(
                            text = "${song.songbook} n°${song.number} | ${song.categories.joinToString { it.libelle }}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // lien officiel
                            if (!song.urlMedia.isNullOrBlank()) {
                                Button(
                                    onClick = {
                                        try {
                                            uriHandler.openUri(song.urlMedia)
                                        } catch (e: Exception) {
                                            // mettre un message lien cassé ou autre
                                            e.printStackTrace()
                                        }
                                    },
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier.width(8.dp))
                                    Text(text = stringResource(R.string.song_detail_button_text))
                                }
                            }
                            // Bouton ajouter un audio
                            OutlinedButton(
                                onClick = onListenClick,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = stringResource(R.string.song_detail_add_audio))
                            }
                        }

                        Spacer(modifier.height(24.dp))

                        // Paroles
                        Text(
                            text = song.lyrics,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5, // Plus aéré
                            textAlign = TextAlign.Start // Ou Start selon les goûts
                        )


                        // Espace pour ne pas être collé au bas de l'écran
                        Spacer(modifier.height(64.dp))
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