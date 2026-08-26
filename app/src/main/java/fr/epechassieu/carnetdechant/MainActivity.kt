package fr.epechassieu.carnetdechant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import fr.epechassieu.carnetdechant.ui.navigation.MainScreen
import fr.epechassieu.carnetdechant.ui.settings.SettingsViewModel
import fr.epechassieu.carnetdechant.ui.theme.CarnetDeChantTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val textSize by settingsViewModel.textSize.collectAsStateWithLifecycle()

            CarnetDeChantTheme(fontScale = textSize.scale) {
                MainScreen(
                    textSize = textSize,
                    onTextSizeChange = settingsViewModel::setTextSize
                )
            }
        }
    }
}