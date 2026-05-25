package com.uade.huellitas.presentation.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uade.huellitas.PatitasPerdidasApplication
import com.uade.huellitas.domain.model.Alert
import com.uade.huellitas.domain.model.Location
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val appContainer = (application as PatitasPerdidasApplication).appContainer
    private val getActiveAlertsUseCase = appContainer.getActiveAlertsUseCase
    private val getAppSettingsUseCase = appContainer.getAppSettingsUseCase
    private val setAlertRadiusUseCase = appContainer.setAlertRadiusUseCase
    private val getCurrentUserUseCase = appContainer.getCurrentUserUseCase
    private val geocodeAddressUseCase = appContainer.geocodeAddressUseCase

    val uiState: StateFlow<MapUiState> = combine(
        getActiveAlertsUseCase(),
        getAppSettingsUseCase(),
        getCurrentUserUseCase()
    ) { alerts, settings, user ->
        Triple(alerts, settings, user)
    }
        .mapLatest { (alerts, settings, user) ->
            val center = user?.location
                ?.takeIf { it.isNotBlank() }
                ?.let { geocodeAddressUseCase(it) }
                ?.let { MapCenter(user.location ?: DEFAULT_CENTER.label, it) }
                ?: DEFAULT_CENTER

            val mapAlerts = alerts
                .map { alert -> alert.toMapAlert(center.location) }
                .filter { alert ->
                    alert.distanceMeters == null || alert.distanceMeters <= settings.alertRadiusKm * 1000
                }

            MapUiState.Success(
                alerts = mapAlerts,
                selectedRadiusKm = settings.alertRadiusKm,
                centerLabel = center.label
            ) as MapUiState
        }
        .catch { e -> emit(MapUiState.Error(e.message ?: "Error al cargar el mapa")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MapUiState.Loading
        )

    fun updateRadius(radiusKm: Int) {
        viewModelScope.launch {
            setAlertRadiusUseCase(radiusKm)
        }
    }

    private fun Alert.toMapAlert(center: Location): MapAlert {
        val preciseLocation = location.latitude != 0.0 || location.longitude != 0.0
        val distanceMeters = if (preciseLocation) {
            haversineDistanceMeters(center.latitude, center.longitude, location.latitude, location.longitude)
        } else {
            null
        }
        val (offsetX, offsetY) = calculateOffsets(center, preciseLocation)

        return MapAlert(
            id = id,
            name = petName,
            typeLabel = if (type.name == "LOST") "PERDIDO" else "ENCONTRADO",
            petType = petType,
            distanceLabel = distanceMeters?.let(::formatDistance) ?: (location.address ?: "Ubicacion sin precision"),
            distanceMeters = distanceMeters,
            colorLabel = color ?: breed ?: size ?: "Sin datos",
            description = description,
            photoUrl = photoUrls.firstOrNull(),
            address = location.address,
            offsetX = offsetX,
            offsetY = offsetY,
            source = this,
            hasPreciseLocation = preciseLocation
        )
    }

    private fun Alert.calculateOffsets(center: Location, preciseLocation: Boolean): Pair<Float, Float> {
        if (!preciseLocation) {
            val hash = id.hashCode()
            val x = 0.18f + ((hash ushr 8) and 0xFF) / 255f * 0.64f
            val y = 0.20f + (hash and 0xFF) / 255f * 0.58f
            return x to y
        }

        val lngDelta = (location.longitude - center.longitude).toFloat()
        val latDelta = (location.latitude - center.latitude).toFloat()
        val x = (0.5f + lngDelta * 45f).coerceIn(0.12f, 0.88f)
        val y = (0.5f - latDelta * 60f).coerceIn(0.16f, 0.84f)
        return x to y
    }

    private fun haversineDistanceMeters(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Int {
        val earthRadius = 6_371_000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val startLat = Math.toRadians(lat1)
        val endLat = Math.toRadians(lat2)

        val a = sin(dLat / 2).let { it * it } +
            cos(startLat) * cos(endLat) * sin(dLon / 2).let { it * it }
        val c = 2 * asin(sqrt(a))
        return (earthRadius * c).roundToInt()
    }

    private fun formatDistance(distanceMeters: Int): String {
        return if (distanceMeters < 1000) {
            "a ${distanceMeters}m"
        } else {
            val distanceKm = distanceMeters / 1000.0
            "a ${"%.1f".format(distanceKm)}km"
        }
    }

    private companion object {
        val DEFAULT_CENTER = MapCenter(
            label = "CABA",
            location = Location(-34.6037, -58.3816, "CABA")
        )
    }
}
