package fr.epechassieu.carnetdechant.ui.importdata

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.epechassieu.carnetdechant.R

/**
 * Composable screen that provides the user interface for importing data.
 *
 * Displays an informative message and a button to trigger the import process.
 * It also handles different UI states such as loading indicators and error messages.
 *
 * @param modifier The [Modifier] to be applied to the screen layout.
 * @param importState The current state of the data import process ([ImportDataUiState]).
 * @param onImportClick Callback function to be invoked when the import button is clicked.
 */
@Composable
fun ImportScreen(
    modifier : Modifier = Modifier,
    importState: ImportDataUiState,
    onImportClick: () -> Unit
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier.height(24.dp))

        Text(
            text = stringResource(R.string.import_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier.height(16.dp))

        Text(
            text = stringResource(R.string.import_message),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier.height(32.dp))

        when (importState) {
            is ImportDataUiState.Loading -> {
                CircularProgressIndicator()
            }
            is ImportDataUiState.Error -> {
                Text(
                    text = stringResource(R.string.import_error_message, importState.message),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier.height(16.dp))
                Button(onClick = onImportClick) {
                    Text(text= stringResource(R.string.import_button_text_again))
                }
            }
            else -> {
                Button(
                    onClick = onImportClick,
                    modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(modifier.width(8.dp))
                    Text(text= stringResource(R.string.import_button_text))
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ImportScreenPreview() {
    ImportScreen(
        importState = ImportDataUiState.Idle,
        onImportClick = {}
    )
}