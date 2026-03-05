package fr.epechassieu.carnetdechant.ui.navigation

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import fr.epechassieu.carnetdechant.ui.importdata.ImportDataViewModel
import fr.epechassieu.carnetdechant.ui.importdata.ImportScreen
import fr.epechassieu.carnetdechant.ui.songlist.SongListContent
import fr.epechassieu.carnetdechant.ui.songlist.SongListViewModel
import fr.epechassieu.carnetdechant.R
import fr.epechassieu.carnetdechant.ui.importdata.ImportDataUiState
import fr.epechassieu.carnetdechant.ui.songdetail.SongDetailScreen
import fr.epechassieu.carnetdechant.ui.songfilter.SongFilterListScreen
import fr.epechassieu.carnetdechant.ui.theme.CarnetDeChantTheme

/**
 * The main entry point for the application's user interface, managing the primary navigation
 * structure and top-level layout components.
 *
 * This composable sets up a [Scaffold] containing:
 * - A [CenterAlignedTopAppBar] displayed on specific primary screens.
 * - A [NavigationBar] (Bottom Bar) providing access to the Song List, Filters, and Import features.
 * - A [NavHost] that handles transitions between various destinations like song details,
 *   listening screens, and data management.
 *
 * @param modifier The [Modifier] to be applied to the root layout of the screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    // recuperation de la destination actuelle
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // détermine si on affiche les barres
    val showBars = currentDestination?.hierarchy?.any { destination ->
        destination.hasRoute(SongListRoute::class) ||
                destination.hasRoute(FilterRoute::class) ||
                destination.hasRoute(ImportRoute::class)
    } ?: false

    // --- Scaffold ---
    Scaffold(
        modifier = modifier,
        topBar = {
            if (showBars) {
                CenterAlignedTopAppBar(
                    title = { Text(text = stringResource(R.string.main_topbar_title)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Liste") },
                    label = { Text(text = stringResource(R.string.main_bottom_bar_list)) },
                    selected = currentDestination?.hasRoute(SongListRoute::class)==true,
                    onClick = {
                        navController.navigate(SongListRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {saveState = false }
                            launchSingleTop = true
                            restoreState=false
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.FilterAlt, contentDescription = "Filtres") },
                    label = { Text(text = stringResource(R.string.main_bottom_bar_filter)) },
                    selected = currentDestination?.hasRoute(FilterRoute::class)==true,
                    onClick = {
                        navController.navigate(FilterRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {saveState = false }
                            launchSingleTop = true
                            restoreState=false
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Download, contentDescription = "Import") },
                    label = { Text(text = stringResource(R.string.main_bottom_bar_import)) },
                    selected = currentDestination?.hasRoute(ImportRoute::class)==true,
                    onClick = {
                        navController.navigate(ImportRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {saveState = false }
                            launchSingleTop = true
                            restoreState=false
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
             navController = navController,
             startDestination = SongListRoute,
             modifier = Modifier.padding(innerPadding)
         ) {

             composable<SongListRoute>{
                 val viewModel: SongListViewModel = hiltViewModel()
                 val state by viewModel.uiState.collectAsStateWithLifecycle()
                 val query by viewModel.searchQuery.collectAsStateWithLifecycle()

                 SongListContent(
                     state = state,
                     searchQuery = query,
                     onSearchQueryChange = viewModel::onSearchQueryChange,
                     onSongClick = { songId ->
                         navController.navigate(SongDetailRoute(songId))
                     }
                 )
             }

             composable<SongDetailRoute> { backStackEntry ->
                 // Récupération des arguments de manière type-safe
                 val route = backStackEntry.toRoute<SongDetailRoute>()

                 SongDetailScreen(
                     songId = route.songId,
                     onBackClick = { navController.popBackStack() },
/*                     onListenClick = { songId ->
                         navController.navigate(ListenRoute(songId))
                     }*/
                 )
             }

            composable<FilterRoute> {
                SongFilterListScreen(
                    onSongClick = { songId ->
                        navController.navigate(SongDetailRoute(songId))
                    }
                )
            }
            composable<ImportRoute> {
                val viewModel: ImportDataViewModel = hiltViewModel()
                val importState by viewModel.uiState.collectAsStateWithLifecycle()

                // Navigation automatique après succès
                LaunchedEffect(importState) {
                    if (importState is ImportDataUiState.Success) {
                        navController.navigate(SongListRoute) {
                            popUpTo(ImportRoute) { inclusive = true }
                        }
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
    CarnetDeChantTheme {
        MainScreen()
    }
}