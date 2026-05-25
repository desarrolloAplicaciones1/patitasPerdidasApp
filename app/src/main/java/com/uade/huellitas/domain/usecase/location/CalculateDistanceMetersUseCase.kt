package com.uade.huellitas.domain.usecase.location

import com.uade.huellitas.domain.model.Location
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

class CalculateDistanceMetersUseCase {

    operator fun invoke(from: Location, to: Location): Int? {
        if (!from.hasPreciseCoordinates() || !to.hasPreciseCoordinates()) {
            return null
        }

        val earthRadiusMeters = 6_371_000.0
        val dLat = Math.toRadians(to.latitude - from.latitude)
        val dLon = Math.toRadians(to.longitude - from.longitude)
        val startLat = Math.toRadians(from.latitude)
        val endLat = Math.toRadians(to.latitude)

        val a = sin(dLat / 2).let { it * it } +
            cos(startLat) * cos(endLat) * sin(dLon / 2).let { it * it }
        val c = 2 * asin(sqrt(a))

        return (earthRadiusMeters * c).roundToInt()
    }

    private fun Location.hasPreciseCoordinates(): Boolean =
        latitude != 0.0 || longitude != 0.0
}
