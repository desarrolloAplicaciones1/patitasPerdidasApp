package com.uade.huellitas.presentation.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uade.huellitas.HuellitasApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SplashUiState {
    object Loading : SplashUiState()
    object NavigateToOnboarding : SplashUiState()
    object NavigateToHome : SplashUiState()
}

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val appContainer = (application as HuellitasApplication).appContainer
    private val isLoggedInUseCase = appContainer.isLoggedInUseCase
    private val syncCurrentUserProfileUseCase = appContainer.syncCurrentUserProfileUseCase

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = if (isLoggedInUseCase()) {
                runCatching { syncCurrentUserProfileUseCase() }
                SplashUiState.NavigateToHome
            } else {
                SplashUiState.NavigateToOnboarding
            }
        }
    }
}
