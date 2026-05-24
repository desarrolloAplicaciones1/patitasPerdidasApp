package com.desarrolloaplicaciones1.patitasperdidas

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.desarrolloaplicaciones1.patitasperdidas.navigation.NavGraph
import com.desarrolloaplicaciones1.patitasperdidas.ui.theme.HuellitasTheme
import com.desarrolloaplicaciones1.patitasperdidas.ui.theme.ThemeState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("huellitas_prefs", Context.MODE_PRIVATE)
        ThemeState.isDarkMode = prefs.getBoolean("dark_mode", false)

        enableEdgeToEdge()
        setContent {
            HuellitasTheme(darkTheme = ThemeState.isDarkMode) {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}