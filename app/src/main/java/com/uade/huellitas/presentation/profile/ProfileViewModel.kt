package com.uade.huellitas.presentation.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uade.huellitas.PatitasPerdidasApplication
import com.uade.huellitas.domain.model.AppSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val appContainer = (application as PatitasPerdidasApplication).appContainer
    private val getCurrentUserUseCase = appContainer.getCurrentUserUseCase
    private val getMyAlertsUseCase = appContainer.getMyAlertsUseCase
    private val getAppSettingsUseCase = appContainer.getAppSettingsUseCase
    private val setDarkModeUseCase = appContainer.setDarkModeUseCase
    private val setAlertRadiusUseCase = appContainer.setAlertRadiusUseCase
    private val setOfflineModeUseCase = appContainer.setOfflineModeUseCase
    private val logoutUseCase = appContainer.logoutUseCase

    val uiState: StateFlow<ProfileUiState> = combine(
        getCurrentUserUseCase(),
        getMyAlertsUseCase()
    ) { user, alerts ->
        if (user != null) {
            ProfileUiState.Success(user = user, userAlerts = alerts) as ProfileUiState
        } else {
            ProfileUiState.Error("No hay un usuario autenticado")
        }
    }
        .catch { e -> emit(ProfileUiState.Error(e.message ?: "Error al cargar perfil")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState.Loading
        )

    val settingsState: StateFlow<AppSettings> = getAppSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings()
        )

    fun logout() {
        logoutUseCase()
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            setDarkModeUseCase(enabled)
        }
    }

    fun setAlertRadius(radiusKm: Int) {
        viewModelScope.launch {
            setAlertRadiusUseCase(radiusKm)
        }
    }

    fun setOfflineMode(enabled: Boolean) {
        viewModelScope.launch {
            setOfflineModeUseCase(enabled)
        }
    }
}
