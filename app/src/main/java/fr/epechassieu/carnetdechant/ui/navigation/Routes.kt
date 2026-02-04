package fr.epechassieu.carnetdechant.ui.navigation

/**
 * Object containing the route definitions and helper functions for application navigation.
 *
 * This centralizes the route strings used by the navigation graph and provides
 * utility methods to build paths that require dynamic arguments.
 */
object Routes {
    const val LIST = "list"
    const val FILTER = "filter"
    const val IMPORT = "import"
    const val DETAILS = "details/{songId}"
    const val LISTEN = "listen/{songId}"

    // Helper dynamic route with id
    fun details(songId: String) = "details/$songId"
    fun listen(songId: String) = "listen/$songId"
}