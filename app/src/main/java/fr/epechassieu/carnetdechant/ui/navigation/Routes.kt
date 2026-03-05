package fr.epechassieu.carnetdechant.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Object containing the route definitions and helper functions for application navigation.
 *
 * This centralizes the route strings used by the navigation graph and provides
 * utility methods to build paths that require dynamic arguments.
 */
/*
object Routes {
    const val LIST = "list"
    const val FILTER = "filter"
    const val IMPORT = "import"
    const val DETAILS = "details/{songId}"
    const val LISTEN = "listen/{songId}"

    // Helper dynamic route with id
    fun details(songId: String) = "details/$songId"
    fun listen(songId: String) = "listen/$songId"
}*/

@Serializable
object SongListRoute

@Serializable
object FilterRoute

@Serializable
object ImportRoute

@Serializable
data class SongDetailRoute(val songId: String)



