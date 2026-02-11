package fr.epechassieu.carnetdechant.domain.exception

sealed class AppException : Exception() {
    // network
    class NetworkError : AppException()

    // imports
    class FileNotFound : AppException()
    class FileCorrupt : AppException()
    data class HttpClientError(val code: Int) : Exception()
    data class ServerError(val code: Int) : Exception()

    //database
    class DatabaseError : AppException()

    //unknown
    data class Unknown(override val message: String?) : Exception()
}