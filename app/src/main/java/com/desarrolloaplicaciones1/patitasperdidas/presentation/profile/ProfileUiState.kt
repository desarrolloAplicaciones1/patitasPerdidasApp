package com.desarrolloaplicaciones1.patitasperdidas.presentation.profile

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Alert
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Pet
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.User

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(
        val user: User,
        val userAlerts: List<Alert> = emptyList()
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

sealed class MyAlertsUiState {
    object Loading : MyAlertsUiState()
    data class Success(val alerts: List<Alert>) : MyAlertsUiState()
    data class Error(val message: String) : MyAlertsUiState()
}

sealed class MyPetsUiState {
    object Loading : MyPetsUiState()
    data class Success(val pets: List<Pet>) : MyPetsUiState()
    data class Error(val message: String) : MyPetsUiState()
}
