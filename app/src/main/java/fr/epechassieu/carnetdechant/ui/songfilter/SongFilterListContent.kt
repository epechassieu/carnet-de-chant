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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.epechassieu.carnetdechant.domain.model.Category
import fr.epechassieu.carnetdechant.ui.songlist.SongItem // On importe ton composant unitaire
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongFilterListContent(
    onSongClick: (String) -> Unit,
    // On utilise viewModel() standard, qui gère Hilt automatiquement maintenant
    viewModel: SongFilterViewModel = hiltViewModel()
) {
    // On observe l'état du ViewModel
    val state by viewModel.uiState.collectAsState()

    // GESTION DU BOUTON RETOUR PHYSIQUE D'ANDROID
    // Si on est dans la liste (category != null), le bouton retour efface la sélection
    // pour revenir à la grille, au lieu de quitter l'application.
    BackHandler(enabled = state.selectedCategory != null) {
        viewModel.clearSelection()
    }

    Scaffold(
        topBar = {
            // LA BARRE DU HAUT CHANGE SELON L'ÉTAT
            if (state.selectedCategory != null) {
                // Écran 2 : Titre de la catégorie (ex: "Louange") + Flèche retour
                CenterAlignedTopAppBar(
                    title = { Text(state.selectedCategory!!.libelle) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            } else {
                // Écran 1 : Titre générique
                CenterAlignedTopAppBar(
                    title = { Text("Par Thèmes") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        }
    ) { innerPadding ->

        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {

            // --- CONDITION D'AFFICHAGE ---
            if (state.selectedCategory == null) {
                // VUE A : LA GRILLE DES CATEGORIES
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // 2 boutons par ligne
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(Category.entries) { category ->
                        Button(
                            onClick = { viewModel.selectCategory(category) },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,   // La couleur du fond
                                contentColor = MaterialTheme.colorScheme.onSecondary    // La couleur du texte
                            )
                        ) {
                            Text(
                                text = category.libelle,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            } else {
                // VUE B : LA LISTE DES CHANTS FILTRÉS
                if (state.filteredSongs.isEmpty()) {
                    Text(
                        text = "Aucun chant dans cette catégorie.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(state.filteredSongs) { song ->
                            // TON COMPOSANT EXISTANT QUI AFFICHE UN CHANT
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

