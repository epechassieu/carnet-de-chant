package fr.epechassieu.carnetdechant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import fr.epechassieu.carnetdechant.ui.navigation.MainScreen
import fr.epechassieu.carnetdechant.ui.theme.CarnetDeChantTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarnetDeChantTheme {
                MainScreen()
            }
        }
    }
}