package com.uade.huellitas.presentation.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(Unit) {
        onNavigateToLogin()
    }
}