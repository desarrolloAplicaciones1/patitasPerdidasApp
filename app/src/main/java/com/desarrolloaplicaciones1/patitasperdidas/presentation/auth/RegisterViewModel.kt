package com.desarrolloaplicaciones1.patitasperdidas.presentation.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.desarrolloaplicaciones1.patitasperdidas.PatitasPerdidasApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val registerUserUseCase =
        (application as PatitasPerdidasApplication).appContainer.registerUserUseCase

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val uid = registerUserUseCase(name, email, password)
                _uiState.value = AuthUiState.Success(uid)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Error al registrarse")
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
