package com.desarrolloaplicaciones1.patitasperdidas.data.repository

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Location
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.GeocodingRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidGeocodingRepository(
    context: Context
) : GeocodingRepository {
    private val geocoder = Geocoder(context.applicationContext)

    override suspend fun geocodeAddress(address: String): Location? {
        if (address.isBlank()) return null

        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocationName(address, 1) { addresses ->
                    continuation.resume(addresses.firstOrNull())
                }
            }
        } else {
            @Suppress("DEPRECATION")
            geocoder.getFromLocationName(address, 1)?.firstOrNull()
        }

        return result?.let {
            Location(
                latitude = it.latitude,
                longitude = it.longitude,
                address = it.getAddressLine(0) ?: address
            )
        }
    }
}
