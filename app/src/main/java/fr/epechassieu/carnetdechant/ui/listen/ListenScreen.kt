package fr.epechassieu.carnetdechant.ui.listen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.remember
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
    val snackbarHostState = remember { SnackbarHostState() }

    // --- monitor error ---
    LaunchedEffect(uiState.error) {
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
        onNewUrlTextChange = viewModel::onNewUrlTextChange,
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
private fun ListenContent(
    modifier: Modifier = Modifier,
    uiState: ListenUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onNewUrlTextChange: (String) -> Unit,
    onAddUrl: () -> Unit,
    onDeleteUrl: (UrlMediaUser) -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                else -> {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        //-- section 1 - official url --
                        if (!uiState.officialUrl.isNullOrBlank()) {
                            Text(
                                text = stringResource(R.string.listen_official_link),
                                modifier = Modifier.padding(top=24.dp),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            LinkCard(
                                url = uiState.officialUrl,
                                onPlayClick = {
                                    openUrlSafe(context, uriHandler, uiState.officialUrl)
                                },
                                onDeleteClick = null
                            )
                        }
                        // -- section 2 - personal url --
                        if (uiState.userUrls.isEmpty()) {
                            Text(
                                text = stringResource(R.string.listen_no_personal_links),
                                modifier = Modifier.padding(top=24.dp),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.listen_my_links),
                                modifier = Modifier.padding(top=24.dp),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            uiState.userUrls.forEach { urlMedia ->
                                LinkCard(
                                    url = urlMedia.url,
                                    onPlayClick = {
                                        openUrlSafe(context, uriHandler, urlMedia.url)
                                    },
                                    onDeleteClick = { onDeleteUrl(urlMedia) }
                                )
                                Spacer(Modifier.height(8.dp))
                            }

                        }
                        // -- section 3 - add url --

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(top=24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            OutlinedTextField(
                                value = uiState.newUrlText,
                                onValueChange = onNewUrlTextChange,
                                modifier = Modifier.weight(1f),
                                placeholder = { Text(stringResource(R.string.listen_add_placeholder)) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Button(
                                onClick = onAddUrl,
                                enabled = uiState.newUrlText.isNotBlank()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LinkCard(
    modifier: Modifier = Modifier,
    url: String,
    onPlayClick: () -> Unit,
    onDeleteClick: (() -> Unit)?
) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = url,
                Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            IconButton(onClick = onPlayClick) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = stringResource(R.string.listen),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

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

private fun openUrlSafe(context: Context, uriHandler: UriHandler, url: String) {
    if (url.isBlank()) return

    try {
        uriHandler.openUri(url)
    } catch (e: Exception) {
        Toast.makeText(
            context,
            context.getString(R.string.error_link_open),
            Toast.LENGTH_SHORT
        )
            .show()
        e.printStackTrace()
    }
}


@Preview(showBackground = true)
@Composable
private fun ListenContentPreview() {
    val fakeState = ListenUiState(
        songTitle = "Dieu est grand",
        officialUrl = "https://youtube.com/watch?v=123",
        userUrls = listOf(
            UrlMediaUser(
                id = 1,
                songId = "1",
                url = "https://youtube.com/ma-version"
            ),
            UrlMediaUser(
                id = 2,
                songId = "1",
                url = "https://spotify.com/autre-lien"
            )
        ),
        isLoading = false
    )
    CarnetDeChantTheme {
        ListenContent(
            uiState = fakeState,
            onBackClick = {},
            onAddUrl = {},
            onDeleteUrl = {},
            onNewUrlTextChange = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}