package com.uade.huellitas.domain.usecase.alert

import com.uade.huellitas.domain.model.Alert
import com.uade.huellitas.domain.model.Location
import com.uade.huellitas.domain.usecase.location.CalculateDistanceMetersUseCase

class FilterAlertsByRadiusUseCase(
    private val calculateDistanceMetersUseCase: CalculateDistanceMetersUseCase
) {

    operator fun invoke(
        alerts: List<Alert>,
        center: Location,
        radiusKm: Int
    ): List<Alert> {
        val radiusMeters = radiusKm * 1000

        return alerts.filter { alert ->
            val distanceMeters = calculateDistanceMetersUseCase(center, alert.location)
            distanceMeters == null || distanceMeters <= radiusMeters
        }
    }
}
