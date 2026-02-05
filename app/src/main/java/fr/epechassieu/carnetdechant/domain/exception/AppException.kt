package fr.epechassieu.carnetdechant.domain.exception

sealed class AppException : Exception() {
    // network
    data object NetworkError : Exception()

    // imports
    data object FileNotFound : Exception()
    data object FileCorrupt : Exception()
    data class HttpClientError(val code: Int) : Exception()
    data class ServerError(val code: Int) : Exception()

    //database
    data object DatabaseError : Exception()

    //unknown
    data class Unknown(override val message: String?) : Exception()
}