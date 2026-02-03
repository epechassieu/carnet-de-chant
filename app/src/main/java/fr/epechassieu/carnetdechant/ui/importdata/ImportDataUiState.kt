package fr.epechassieu.carnetdechant.ui.importdata


    sealed interface ImportDataUiState {
        data object Idle : ImportDataUiState
        data object Loading : ImportDataUiState
        data object Success : ImportDataUiState
        data class Error(val message: String) : ImportDataUiState
    }
