package fr.epechassieu.carnetdechant.ui.listen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import fr.epechassieu.carnetdechant.R
import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser
import fr.epechassieu.carnetdechant.ui.theme.CarnetDeChantTheme


/**
 * Screen that allows users to listen to a song via official and personal media links.
 *
 * It manages the display of the song title, provides an interface to open external URLs,
 * and allows users to add or remove their own custom media links.
 *
 * @param onBackClick Callback invoked when the user navigates back.
 * @param viewModel The [ListenViewModel] that provides the screen state and handles business logic.
 */
@Composable
fun ListenScreen(
    onBackClick: () -> Unit,
    viewModel: ListenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    /*val context = LocalContext.current*/
    val snackbarHostState = remember { SnackbarHostState() }

    // --- monitor error ---
    LaunchedEffect(uiState.error){
        uiState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    // --- designer ---
    ListenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onAddUrl = viewModel::addUrl,
        onDeleteUrl = viewModel::deleteUrl,
        snackbarHostState = snackbarHostState
    )
}

/**
 * Stateless version of the Listen screen that displays the song title and media links.
 *
 * It handles different UI states (loading, error, or success) and provides the layout for:
 * - The top app bar with the song title and back navigation.
 * - The official media link section.
 * - The list of user-added media links with play and delete actions.
 * - An input field to add new custom media URLs.
 *
 * @param uiState The current state of the UI to be displayed.
 * @param onBackClick Callback invoked when the back button is pressed.
 * @param onAddUrl Callback invoked when the user adds a new URL.
 * @param onDeleteUrl Callback invoked when the user requests to delete a specific custom link.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListenContent(
    uiState: ListenUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onAddUrl: (String) -> Unit,
    onDeleteUrl: (UrlMediaUser) -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var newUrlText by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState)},
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = uiState.songTitle.ifEmpty { stringResource(R.string.loading) },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (!uiState.isLoading)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // Section lien officiel
                    if (!uiState.officialUrl.isNullOrBlank()) {
                        item {
                            Text(
                                text = stringResource(R.string.listen_official_link),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinkCard(
                                url = uiState.officialUrl,
                                onPlayClick = {
/*                                        try {
                                            uriHandler.openUri(uiState.officialUrl)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }*/
                                    openUrlSafe(context, uriHandler, uiState.officialUrl)

                                },
                                onDeleteClick = null // Pas de suppression pour le lien officiel
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    // Section liens utilisateur
                    item {
                        Text(
                            text = stringResource(R.string.listen_my_links),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Liste des liens utilisateur
                    items(
                        items = uiState.userUrls,
                        key = { it.id }
                    ) { urlMedia ->
                        LinkCard(
                            url = urlMedia.url,
                            onPlayClick = {
                                openUrlSafe(context, uriHandler, urlMedia.url)

                            },
                            onDeleteClick = { onDeleteUrl(urlMedia) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Message si aucun lien utilisateur
                    if (uiState.userUrls.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.listen_no_personal_links),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Zone d'ajout de lien
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newUrlText,
                                onValueChange = { newUrlText = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text(stringResource(R.string.listen_add_placeholder)) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    onAddUrl(newUrlText)
                                    newUrlText = ""
                                },
                                enabled = newUrlText.isNotBlank()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                        }
                    }
                }
        }
        if (uiState.isLoading) {
            CircularProgressIndicator()
        }
    }
}


@Composable
private fun LinkCard(
    url: String,
    onPlayClick: () -> Unit,
    onDeleteClick: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // URL (tronquée si trop longue)
            Text(
                text = url,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Bouton play
            IconButton(onClick = onPlayClick) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = stringResource(R.string.listen),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Bouton supprimer (seulement pour les liens utilisateur)
            if (onDeleteClick != null) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

// --- try catch for Open Url.---

fun openUrlSafe(context: Context, uriHandler: UriHandler, url: String) {
    if (url.isBlank()) return

    try {
        uriHandler.openUri(url)
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.error_link_open), Toast.LENGTH_SHORT)
            .show()
        e.printStackTrace()
    }
}

@Preview(showBackground = true, device = "id:pixel_9")
@Composable
fun ListenContentPreview() {
    val fakeState = ListenUiState(
        songTitle = "Dieu est grand",
        officialUrl = "https://youtube.com/watch?v=123",
        userUrls = listOf(
            UrlMediaUser(id = 1, songId = "1", url = "https://youtube.com/ma-version"),
            UrlMediaUser(id = 2, songId = "1", url = "https://spotify.com/autre-lien")
        ),
        isLoading = false
    )
    CarnetDeChantTheme {
        ListenContent(
            uiState = fakeState,
            onBackClick = {},
            onAddUrl = {},
            onDeleteUrl = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}