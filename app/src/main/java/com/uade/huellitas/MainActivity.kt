package com.uade.huellitas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.uade.huellitas.domain.model.AppSettings
import com.uade.huellitas.navigation.NavGraph
import com.uade.huellitas.ui.theme.HuellitasTheme
import com.uade.huellitas.ui.theme.ThemeState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val appContainer = (application as HuellitasApplication).appContainer
            val settings = appContainer.getAppSettingsUseCase()
                .collectAsStateWithLifecycle(initialValue = AppSettings())
            val resolvedDarkMode = if (settings.value.followSystemTheme) {
                isSystemInDarkTheme()
            } else {
                settings.value.darkModeEnabled
            }
            ThemeState.isDarkMode = resolvedDarkMode

            HuellitasTheme(darkTheme = resolvedDarkMode) {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
