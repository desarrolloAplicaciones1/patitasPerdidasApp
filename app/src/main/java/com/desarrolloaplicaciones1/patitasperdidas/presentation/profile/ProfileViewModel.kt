package com.desarrolloaplicaciones1.patitasperdidas.presentation.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.desarrolloaplicaciones1.patitasperdidas.PatitasPerdidasApplication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val appContainer = (application as PatitasPerdidasApplication).appContainer
    private val getCurrentUserUseCase = appContainer.getCurrentUserUseCase
    private val logoutUseCase = appContainer.logoutUseCase

    val uiState: StateFlow<ProfileUiState> = getCurrentUserUseCase()
        .filterNotNull()
        .map { user -> ProfileUiState.Success(user) as ProfileUiState }
        .catch { e -> emit(ProfileUiState.Error(e.message ?: "Error al cargar perfil")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState.Loading
        )

    fun logout() {
        logoutUseCase()
    }
}
