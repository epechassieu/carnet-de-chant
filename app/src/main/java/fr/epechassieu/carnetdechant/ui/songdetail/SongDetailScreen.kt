package fr.epechassieu.carnetdechant.ui.songdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailScreen(
    onBackClick: () -> Unit,
    viewModel: SongDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val titleText =
                        if (uiState is SongDetailUiState.Success) (uiState as SongDetailUiState.Success).song.title else "Chargement..."
                    Text(
                        titleText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
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
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is SongDetailUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )

                is SongDetailUiState.Error -> Text(
                    "Chant introuvable",
                    modifier = Modifier.align(Alignment.Center)
                )

                is SongDetailUiState.Success -> {
                    val song = state.song

                    // --- LE CONTENU DU CHANT ---
                    Column(
                        modifier = Modifier
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

                        Spacer(modifier = Modifier.height(8.dp))

                        // lien vers youtube
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
                                Spacer(modifier=Modifier.width(8.dp))
                                Text(text = "Ecouter le chant")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Paroles
                        Text(
                            text = song.lyrics,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5, // Plus aéré
                            textAlign = TextAlign.Start // Ou Start selon tes goûts
                        )


                        // Espace pour ne pas être collé au bas de l'écran
                        Spacer(modifier = Modifier.height(64.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_9")
@Composable
fun SongDetailScreenPreview() {
    SongDetailScreen(
        onBackClick = {}
    )
}