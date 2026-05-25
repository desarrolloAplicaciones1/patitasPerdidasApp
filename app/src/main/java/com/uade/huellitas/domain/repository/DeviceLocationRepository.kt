package com.uade.huellitas.domain.repository

import com.uade.huellitas.domain.model.DeviceLocationResult

interface DeviceLocationRepository {
    suspend fun getCurrentLocation(): DeviceLocationResult
}
