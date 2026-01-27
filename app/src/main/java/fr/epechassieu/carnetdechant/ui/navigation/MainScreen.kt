package fr.epechassieu.carnetdechant.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.epechassieu.carnetdechant.ui.importdata.ImportDataViewModel
import fr.epechassieu.carnetdechant.ui.importdata.ImportScreen
import fr.epechassieu.carnetdechant.ui.songlist.SongListContent
import fr.epechassieu.carnetdechant.ui.songlist.SongListViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import fr.epechassieu.carnetdechant.ui.songdetail.SongDetailScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // Permet de savoir sur quel écran on est pour colorer le bouton du bas
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        // 1. La Barre du Haut (Titre)
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Carnet de Chant") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        // 2. La Barre du Bas (Menu)
        bottomBar = {
            NavigationBar {
                // Onglet LISTE
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Liste") },
                    label = { Text("Chants") },
                    selected = currentRoute == "list",
                    onClick = {
                        navController.navigate("list") {
                            // Évite d'empiler les écrans si on clique plusieurs fois
                            popUpTo("list") { inclusive = true }
                        }
                    }
                )
                // Onglet FILTRE (Vide pour l'instant)
                NavigationBarItem(
                    icon = { Icon(Icons.Default.FilterAlt, contentDescription = "Filtres") },
                    label = { Text("Filtres") },
                    selected = currentRoute == "filter",
                    onClick = { navController.navigate("filter") }
                )
                // Onglet IMPORT
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Download, contentDescription = "Import") },
                    label = { Text("Import") },
                    selected = currentRoute == "import",
                    onClick = { navController.navigate("import") }
                )
            }
        }
    ) { innerPadding ->
        // 3. Le Contenu qui change (NavHost)
        NavHost(
            navController = navController,
            startDestination = "list",
            modifier = Modifier.padding(innerPadding)
        ) {

            // --- ROUTE  : LA LISTE DES CHANTS ---
            composable("list") {
                // Injection du Cerveau "Liste"
                val viewModel: SongListViewModel = hiltViewModel()
                val state by viewModel.uiState.collectAsState()
                val query by viewModel.searchQuery.collectAsState()

                SongListContent(
                    state = state,
                    searchQuery = query,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onSongClick = { songId ->
                        navController.navigate("details/$songId")
                    }
                )
            }

            // --- ROUTE  : LE DÉTAIL ---
            composable(
                "details/{songId}",
                arguments = listOf(navArgument("songId") { type = NavType.StringType })
            ){
                SongDetailScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // --- ROUTE  : LES FILTRES (À faire) ---
            composable("filter") {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Page des filtres (À venir)")
                }
            }

            // --- ROUTE  : L'IMPORT ---
            composable("import") {
                // Injection du Cerveau "ImportData" (le nouveau package !)
                val viewModel: ImportDataViewModel = hiltViewModel()

                ImportScreen(
                    onImportClick = {
                        viewModel.importSongs()
                        // Une fois lancé, on retourne à la liste
                        navController.navigate("list")
                    }
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}