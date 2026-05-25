package com.desarrolloaplicaciones1.patitasperdidas.presentation.create

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.AlertType
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.PetType

sealed class ExpressAlertUiState {
    object Idle : ExpressAlertUiState()
    object Loading : ExpressAlertUiState()
    object Success : ExpressAlertUiState()
    data class Error(val message: String) : ExpressAlertUiState()
}

data class ExpressAlertFormState(
    val alertType: AlertType = AlertType.LOST,
    val petName: String = "",
    val petType: PetType = PetType.DOG,
    val size: String = "Chico",
    val description: String = "",
    val address: String = ""
)
