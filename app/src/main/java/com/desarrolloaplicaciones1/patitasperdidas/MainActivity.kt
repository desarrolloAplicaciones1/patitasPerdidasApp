package com.desarrolloaplicaciones1.patitasperdidas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.desarrolloaplicaciones1.patitasperdidas.navigation.NavGraph
import com.desarrolloaplicaciones1.patitasperdidas.ui.theme.PatitasPerdidasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PatitasPerdidasTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
