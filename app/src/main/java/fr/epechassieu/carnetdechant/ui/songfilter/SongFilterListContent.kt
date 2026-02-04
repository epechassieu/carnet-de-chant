package fr.epechassieu.carnetdechant.ui.songfilter

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.ui.songlist.SongItem
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import fr.epechassieu.carnetdechant.R
import fr.epechassieu.carnetdechant.domain.model.Song
import fr.epechassieu.carnetdechant.ui.theme.CarnetDeChantTheme


/**
 * Main entry point for the song filtering screen.
 *
 * This composable connects the [SongFilterViewModel] to the [SongFilterListContent] UI.
 * It manages the display of song categories and the subsequent filtered list of songs
 * based on the user's selection.
 *
 * @param onSongClick Callback invoked when a specific song is selected, passing the song's ID.
 * @param viewModel The ViewModel handling the business logic and UI state for filtering.
 */
@Composable
fun SongFilterListScreen(
    onSongClick: (String) -> Unit,
    viewModel: SongFilterViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // On passe juste les données et les actions au composant "bête"
    SongFilterListContent(
        state = state,
        onSongClick = onSongClick,
        onCategorySelect = viewModel::selectCategory,
        onClearSelection = viewModel::clearSelection
    )
}

/**
 * A stateless composable that displays the UI for the song filtering screen.
 * It can show either a grid of song categories or a list of songs filtered by a selected category.
 *
 * This component is "dumb" as it only displays the data provided in the [state] and delegates
 * user actions to the provided lambda functions.
 *
 * It handles the Android back button press: if a category is selected (showing the song list),
 * the back button will clear the selection and return to the category grid instead of navigating
 * away from the screen.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param state The current state of the UI, containing the list of categories, the selected category, and the list of filtered songs.
 * @param onSongClick A callback invoked when a song from the filtered list is clicked. It provides the song's ID.
 * @param onCategorySelect A callback invoked when a category from the grid is selected.
 * @param onClearSelection A callback invoked to clear the current category selection, typically to return to the category grid.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongFilterListContent(
    modifier : Modifier = Modifier,
    state: SongFilterUiState,
    onSongClick: (String) -> Unit,
    onCategorySelect: (Category) -> Unit,
    onClearSelection: () -> Unit
) {

    // GESTION DU BOUTON RETOUR PHYSIQUE D'ANDROID
    // Si on est dans la liste (category != null), le bouton retour efface la sélection
    // pour revenir à la grille, au lieu de quitter l'application.
    BackHandler(enabled = state.selectedCategory != null) {
        onClearSelection()
    }

    Scaffold(
        topBar = {
            // LA BARRE DU HAUT CHANGE SELON L'ÉTAT
            if (state.selectedCategory != null) {
                // Écran 2 : Titre de la catégorie (ex: "Louange") + Flèche retour
                CenterAlignedTopAppBar(
                    title = { Text(state.selectedCategory.libelle) },
                    navigationIcon = {
                        IconButton(onClick = onClearSelection) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.song_backclic))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            } else {
                // VUE GRILLE : Titre générique
                CenterAlignedTopAppBar(
                    title = { Text(text=stringResource(R.string.song_filter_title_theme)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            // --- CONDITION D'AFFICHAGE ---
            if (state.selectedCategory == null) {
                // VUE A : LA GRILLE DES CATEGORIES
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.categories) { category ->
                        Button(
                            onClick = { onCategorySelect(category) },
                            modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = category.libelle,
                                modifier.padding(8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

            } else {
                // VUE B : LA LISTE DES CHANTS FILTRÉS
                if (state.filteredSongs.isEmpty()) {
                    Text(
                        text = stringResource(R.string.song_filter_is_empty),
                        modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items =state.filteredSongs,
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

@Preview(
    showSystemUi = true,
    showBackground = true,
    name = "1 vue grille theme",
    device = "id:pixel_9"
)
@Composable
fun PreviewFilterGruid() {
    CarnetDeChantTheme {
        SongFilterListContent(
            state = SongFilterUiState(selectedCategory = null, filteredSongs = emptyList()),
            onSongClick = {},
            onCategorySelect= {},
            onClearSelection = {}
        )
    }
}


@Preview(
    showSystemUi = true,
    showBackground = true,
    name = "2 vue liste selon theme",
    device = "id:pixel_9"
)
@Composable
fun PreviewFilterList() {
    // Liste de fake songs pour preview
    val fakeSongs = listOf(
        Song(
            id = "1",
            songbook = "JEM",
            number = 100,
            title = "Dieu est grand",
            categories = listOf(Category.LOUANGE),
            lyrics = "Test",
            urlMedia = ""
        ),
        Song(
            id = "2",
            songbook = "ATG",
            number = 42,
            title = "Jésus t'aime",
            categories = listOf(Category.ADORATION),
            lyrics = "Test",
            urlMedia = ""
        )
    )
    CarnetDeChantTheme {
        SongFilterListContent(
            state = SongFilterUiState(
                selectedCategory = Category.LOUANGE,
                filteredSongs = fakeSongs
            ),
            onSongClick = {},
            onCategorySelect = {},
            onClearSelection = {}
        )
    }
}

