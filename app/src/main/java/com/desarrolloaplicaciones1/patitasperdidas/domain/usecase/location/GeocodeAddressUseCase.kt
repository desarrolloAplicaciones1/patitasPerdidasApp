package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.location

import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.GeocodingRepository

class GeocodeAddressUseCase(
    private val geocodingRepository: GeocodingRepository
) {
    suspend operator fun invoke(address: String) =
        geocodingRepository.geocodeAddress(address)
}
