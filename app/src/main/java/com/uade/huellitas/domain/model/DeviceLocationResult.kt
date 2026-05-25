package com.uade.huellitas.domain.model

sealed interface DeviceLocationResult {
    data class Available(val location: Location) : DeviceLocationResult
    data object PermissionDenied : DeviceLocationResult
    data object Unavailable : DeviceLocationResult
}
