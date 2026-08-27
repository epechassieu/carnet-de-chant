package fr.epechassieu.carnetdechant.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import fr.epechassieu.carnetdechant.R
import fr.epechassieu.carnetdechant.domain.model.TextSize

/**
 * Menu déroulant permettant de choisir la taille du texte de l'application.
 * Réutilisable dans n'importe quelle topbar (écran principal, détail d'un chant…).
 */
@Composable
fun TextSizeMenu(
    currentTextSize: TextSize,
    onTextSizeChange: (TextSize) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.FormatSize,
            contentDescription = stringResource(R.string.main_text_size_menu)
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        TextSize.entries.forEach { size ->
            DropdownMenuItem(
                text = { Text(text = textSizeLabel(size)) },
                trailingIcon = {
                    if (size == currentTextSize) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                },
                onClick = {
                    onTextSizeChange(size)
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun textSizeLabel(textSize: TextSize): String = when (textSize) {
    TextSize.PETIT -> stringResource(R.string.main_text_size_small)
    TextSize.NORMAL -> stringResource(R.string.main_text_size_normal)
    TextSize.GRAND -> stringResource(R.string.main_text_size_large)
    TextSize.TRES_GRAND -> stringResource(R.string.main_text_size_extra_large)
}
