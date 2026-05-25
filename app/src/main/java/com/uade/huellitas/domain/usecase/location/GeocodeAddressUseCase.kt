package com.uade.huellitas.domain.usecase.location

import com.uade.huellitas.domain.repository.GeocodingRepository

class GeocodeAddressUseCase(
    private val geocodingRepository: GeocodingRepository
) {
    suspend operator fun invoke(address: String) =
        geocodingRepository.geocodeAddress(address)
}
