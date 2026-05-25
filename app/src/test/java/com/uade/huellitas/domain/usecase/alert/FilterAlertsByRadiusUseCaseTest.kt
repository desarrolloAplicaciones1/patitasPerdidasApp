package com.uade.huellitas.domain.usecase.alert

import com.uade.huellitas.domain.model.Location
import com.uade.huellitas.domain.usecase.location.CalculateDistanceMetersUseCase
import com.uade.huellitas.makeAlert
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterAlertsByRadiusUseCaseTest {

    private val filterAlertsByRadiusUseCase =
        FilterAlertsByRadiusUseCase(CalculateDistanceMetersUseCase())

    @Test
    fun `keeps only alerts inside the requested radius when coordinates are precise`() {
        val center = Location(-34.6037, -58.3816)
        val alerts = listOf(
            makeAlert(id = "near").copy(location = Location(-34.6040, -58.3820)),
            makeAlert(id = "far").copy(location = Location(-34.6500, -58.5000))
        )

        val result = filterAlertsByRadiusUseCase(
            alerts = alerts,
            center = center,
            radiusKm = 3
        )

        assertEquals(listOf("near"), result.map { it.id })
    }

    @Test
    fun `keeps alerts without precise coordinates to avoid hiding local cached data`() {
        val center = Location(-34.6037, -58.3816)
        val alerts = listOf(
            makeAlert(id = "imprecise").copy(location = Location(0.0, 0.0, "Sin coordenadas"))
        )

        val result = filterAlertsByRadiusUseCase(
            alerts = alerts,
            center = center,
            radiusKm = 1
        )

        assertEquals(listOf("imprecise"), result.map { it.id })
    }
}
