package com.uade.huellitas.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.location.LocationManagerCompat
import androidx.core.os.CancellationSignal
import com.uade.huellitas.domain.model.DeviceLocationResult
import com.uade.huellitas.domain.model.Location
import com.uade.huellitas.domain.repository.DeviceLocationRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidDeviceLocationRepository(
    context: Context
) : DeviceLocationRepository {

    private val appContext = context.applicationContext
    private val locationManager =
        appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override suspend fun getCurrentLocation(): DeviceLocationResult {
        if (!hasLocationPermission()) {
            return DeviceLocationResult.PermissionDenied
        }

        val fallbackLocation = getBestLastKnownLocation()
        val provider = getBestProvider()
            ?: return fallbackLocation?.let(::availableLocation)
            ?: DeviceLocationResult.Unavailable

        return requestCurrentLocation(provider, fallbackLocation)
    }

    private fun hasLocationPermission(): Boolean {
        val finePermission = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarsePermission = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        return finePermission == PermissionChecker.PERMISSION_GRANTED ||
            coarsePermission == PermissionChecker.PERMISSION_GRANTED
    }

    private fun getBestProvider(): String? = when {
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
            LocationManager.GPS_PROVIDER

        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
            LocationManager.NETWORK_PROVIDER

        else -> null
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestCurrentLocation(
        provider: String,
        fallbackLocation: android.location.Location?
    ): DeviceLocationResult = suspendCancellableCoroutine { continuation ->
        val cancellationSignal = CancellationSignal()
        continuation.invokeOnCancellation { cancellationSignal.cancel() }

        LocationManagerCompat.getCurrentLocation(
            locationManager,
            provider,
            cancellationSignal,
            ContextCompat.getMainExecutor(appContext)
        ) { location ->
            val resolvedLocation = location ?: fallbackLocation
            continuation.resume(
                resolvedLocation?.let(::availableLocation) ?: DeviceLocationResult.Unavailable
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getBestLastKnownLocation(): android.location.Location? =
        locationManager.getProviders(true)
            .asSequence()
            .mapNotNull { provider ->
                runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull()
            }
            .maxByOrNull { location -> location.time }

    private fun availableLocation(location: android.location.Location): DeviceLocationResult =
        DeviceLocationResult.Available(
            Location(
                latitude = location.latitude,
                longitude = location.longitude
            )
        )
}
