package com.uade.huellitas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
            val appContainer = (application as PatitasPerdidasApplication).appContainer
            val settings = appContainer.getAppSettingsUseCase()
                .collectAsStateWithLifecycle(initialValue = AppSettings())
            ThemeState.isDarkMode = settings.value.darkModeEnabled

            HuellitasTheme(darkTheme = settings.value.darkModeEnabled) {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
