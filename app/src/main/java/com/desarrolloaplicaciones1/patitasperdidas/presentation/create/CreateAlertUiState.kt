package com.desarrolloaplicaciones1.patitasperdidas.presentation.create

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.AlertType
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.PetType

sealed class CreateAlertUiState {
    object Idle                           : CreateAlertUiState()
    object Loading                        : CreateAlertUiState()
    object Success                        : CreateAlertUiState()
    data class Error(val message: String) : CreateAlertUiState()
}

data class CreateAlertFormState(
    val alertType: AlertType = AlertType.LOST,
    val petName: String = "",
    val petType: PetType = PetType.DOG,
    val breed: String = "",
    val color: String = "",
    val description: String = "",
    val contactPhone: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String = ""
)
