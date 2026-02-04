package fr.epechassieu.carnetdechant.ui.importdata


    /**
     * Represents the various UI states for the data import process.
     *
     * @property Idle The initial state before any import operation has started.
     * @property Loading Indicates that the data import is currently in progress.
     * @property Success Indicates that the data import has completed successfully.
     * @property Error Represents a failure during the import process, containing an error [message].
     */
    sealed interface ImportDataUiState {
        data object Idle : ImportDataUiState
        data object Loading : ImportDataUiState
        data object Success : ImportDataUiState
        data class Error(val message: String) : ImportDataUiState
    }
