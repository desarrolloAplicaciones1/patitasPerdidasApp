package com.desarrolloaplicaciones1.patitasperdidas.presentation.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Login")
    }
}
