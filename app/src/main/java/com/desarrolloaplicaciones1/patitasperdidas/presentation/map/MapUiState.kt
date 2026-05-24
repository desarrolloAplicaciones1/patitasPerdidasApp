package com.desarrolloaplicaciones1.patitasperdidas.presentation.map

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Alert
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Location
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.PetType

sealed class MapUiState {
    object Loading : MapUiState()
    data class Success(
        val alerts: List<MapAlert>,
        val selectedRadiusKm: Int,
        val centerLabel: String
    ) : MapUiState()
    data class Error(val message: String) : MapUiState()
}

data class MapAlert(
    val id: String,
    val name: String,
    val typeLabel: String,
    val petType: PetType,
    val distanceLabel: String,
    val distanceMeters: Int?,
    val colorLabel: String,
    val description: String,
    val photoUrl: String?,
    val address: String?,
    val offsetX: Float,
    val offsetY: Float,
    val source: Alert,
    val hasPreciseLocation: Boolean
)

data class MapCenter(
    val label: String,
    val location: Location
)
