package com.uade.huellitas.presentation.profile.alerts

import com.uade.huellitas.domain.model.Alert

sealed class MyAlertsUiState {
    object Loading : MyAlertsUiState()
    object Empty : MyAlertsUiState()
    data class Success(val alerts: List<Alert>) : MyAlertsUiState()
    data class Error(val message: String) : MyAlertsUiState()
}
