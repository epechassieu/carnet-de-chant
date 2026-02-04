package fr.epechassieu.carnetdechant.ui.navigation

object Routes {
    const val LIST = "list"
    const val FILTER = "filter"
    const val IMPORT = "import"
    const val DETAILS = "details/{songId}"
    const val LISTEN = "listen/{songId}"

    // Helper pour construire la route avec l'id
    fun details(songId: String) = "details/$songId"
    fun listen(songId: String) = "listen/$songId"
}