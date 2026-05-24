package com.desarrolloaplicaciones1.patitasperdidas.presentation.home

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Alert
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.AlertType
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.PetType

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val alerts: List<Alert>,
        val currentUserName: String? = null
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

data class HomeFilterState(
    val petType: PetType? = null,
    val alertType: AlertType? = null,
    val radiusKm: Int = 10
)
