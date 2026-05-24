package com.desarrolloaplicaciones1.patitasperdidas.domain.repository

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Location

interface GeocodingRepository {
    suspend fun geocodeAddress(address: String): Location?
}
