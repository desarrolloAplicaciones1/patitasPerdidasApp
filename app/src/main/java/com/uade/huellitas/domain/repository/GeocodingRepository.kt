package com.uade.huellitas.domain.repository

import com.uade.huellitas.domain.model.Location

interface GeocodingRepository {
    suspend fun geocodeAddress(address: String): Location?
}
