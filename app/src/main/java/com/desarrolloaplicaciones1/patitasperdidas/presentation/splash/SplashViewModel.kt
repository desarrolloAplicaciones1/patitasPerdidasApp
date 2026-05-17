package com.desarrolloaplicaciones1.patitasperdidas.presentation.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.desarrolloaplicaciones1.patitasperdidas.data.local.AppDatabase
import com.desarrolloaplicaciones1.patitasperdidas.data.network.FirebaseAuthDataSource
import com.desarrolloaplicaciones1.patitasperdidas.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class SplashUiState {
    object Loading              : SplashUiState()
    object NavigateToOnboarding : SplashUiState()
    object NavigateToHome       : SplashUiState()
}

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository.getInstance(
        AppDatabase.getInstance(application).userDao(),
        FirebaseAuthDataSource()
    )

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = if (userRepository.isLoggedIn()) {
            SplashUiState.NavigateToHome
        } else {
            SplashUiState.NavigateToOnboarding
        }
    }
}
