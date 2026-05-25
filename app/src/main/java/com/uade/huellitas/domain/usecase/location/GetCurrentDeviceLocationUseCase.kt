package com.uade.huellitas.domain.usecase.location

import com.uade.huellitas.domain.repository.DeviceLocationRepository

class GetCurrentDeviceLocationUseCase(
    private val deviceLocationRepository: DeviceLocationRepository
) {
    suspend operator fun invoke() = deviceLocationRepository.getCurrentLocation()
}
