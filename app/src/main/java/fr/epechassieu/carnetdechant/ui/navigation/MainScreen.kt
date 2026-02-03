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
import androidx.compose.runtime.LaunchedEffect
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
import fr.epechassieu.carnetdechant.ui.importdata.ImportDataUiState
import fr.epechassieu.carnetdechant.ui.songdetail.SongDetailScreen
import fr.epechassieu.carnetdechant.ui.songfilter.SongFilterListContent
import fr.epechassieu.carnetdechant.ui.songfilter.SongFilterListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Écrans qui affichent la TopAppBar
    val showBars = currentRoute in listOf(Routes.LIST, Routes.IMPORT)

    Scaffold(
        topBar = {
            if (showBars) {
                CenterAlignedTopAppBar(
                    title = { Text("Carnet de Chant") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        },
        bottomBar = {
            // BottomBar toujours visible
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Liste") },
                    label = { Text("Chants") },
                    selected = currentRoute == Routes.LIST,
                    onClick = {
                        navController.navigate(Routes.LIST) {
                            popUpTo(Routes.LIST) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.FilterAlt, contentDescription = "Filtres") },
                    label = { Text("Filtres") },
                    selected = currentRoute == Routes.FILTER,
                    onClick = {
                        navController.navigate(Routes.FILTER) {
                            popUpTo(Routes.LIST)
                            launchSingleTop = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Download, contentDescription = "Import") },
                    label = { Text("Import") },
                    selected = currentRoute == Routes.IMPORT,
                    onClick = {
                        navController.navigate(Routes.IMPORT) {
                            popUpTo(Routes.LIST)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LIST,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Liste des chants
            composable(Routes.LIST) {
                val viewModel: SongListViewModel = hiltViewModel()
                val state by viewModel.uiState.collectAsState()
                val query by viewModel.searchQuery.collectAsState()

                SongListContent(
                    state = state,
                    searchQuery = query,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onSongClick = { songId ->
                        navController.navigate(Routes.details(songId))
                    }
                )
            }

            // Détail du chant
            composable(
                Routes.DETAILS,
                arguments = listOf(navArgument("songId") { type = NavType.StringType })
            ) {
                SongDetailScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Filtre par catégorie
            composable(Routes.FILTER) {
                SongFilterListScreen(
                    onSongClick = { songId ->
                        navController.navigate(Routes.details(songId))
                    }
                )
            }

            // Import
            composable(Routes.IMPORT) {
                val viewModel: ImportDataViewModel = hiltViewModel()
                val importState by viewModel.uiState.collectAsState()

                // Navigation automatique après succès
                LaunchedEffect(importState) {
                    if (importState is ImportDataUiState.Success) {
                        navController.navigate(Routes.LIST)
                    }
                }

                ImportScreen(
                    importState = importState,
                    onImportClick = { viewModel.importSongs() }
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