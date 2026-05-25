package com.uade.huellitas.domain.usecase.location

import com.uade.huellitas.domain.model.DeviceLocationResult
import com.uade.huellitas.domain.model.Location
import com.uade.huellitas.domain.model.ReferenceLocation
import com.uade.huellitas.domain.model.ReferenceLocationSource

class ResolveReferenceLocationUseCase(
    private val getCurrentDeviceLocationUseCase: GetCurrentDeviceLocationUseCase,
    private val geocodeAddressUseCase: GeocodeAddressUseCase
) {

    suspend operator fun invoke(userLocation: String?): ReferenceLocation {
        when (val result = getCurrentDeviceLocationUseCase()) {
            is DeviceLocationResult.Available -> {
                return ReferenceLocation(
                    label = CURRENT_DEVICE_LABEL,
                    location = result.location,
                    source = ReferenceLocationSource.CURRENT_DEVICE
                )
            }

            DeviceLocationResult.PermissionDenied,
            DeviceLocationResult.Unavailable -> Unit
        }

        if (!userLocation.isNullOrBlank()) {
            geocodeAddressUseCase(userLocation)?.let { location ->
                return ReferenceLocation(
                    label = userLocation,
                    location = location,
                    source = ReferenceLocationSource.USER_PROFILE
                )
            }
        }

        return ReferenceLocation(
            label = DEFAULT_LABEL,
            location = DEFAULT_LOCATION,
            source = ReferenceLocationSource.DEFAULT
        )
    }

    companion object {
        const val CURRENT_DEVICE_LABEL = "Tu ubicacion actual"
        const val DEFAULT_LABEL = "CABA"
        val DEFAULT_LOCATION = Location(
            latitude = -34.6037,
            longitude = -58.3816,
            address = DEFAULT_LABEL
        )
    }
}
