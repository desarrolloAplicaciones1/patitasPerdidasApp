package com.uade.huellitas.domain.usecase.location

import com.uade.huellitas.domain.model.Location
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateDistanceMetersUseCaseTest {

    private val calculateDistanceMetersUseCase = CalculateDistanceMetersUseCase()

    @Test
    fun `returns null when origin location is not precise`() {
        val distance = calculateDistanceMetersUseCase(
            from = Location(0.0, 0.0),
            to = Location(-34.6037, -58.3816)
        )

        assertNull(distance)
    }

    @Test
    fun `returns zero when both locations are the same`() {
        val buenosAires = Location(-34.6037, -58.3816)

        val distance = calculateDistanceMetersUseCase(
            from = buenosAires,
            to = buenosAires
        )

        assertEquals(0, distance)
    }

    @Test
    fun `returns a distance in meters when both locations are precise`() {
        val distance = calculateDistanceMetersUseCase(
            from = Location(-34.6037, -58.3816),
            to = Location(-34.6158, -58.4333)
        )

        assertNotNull(distance)
        checkNotNull(distance)
        assertTrue(distance > 1_000)
    }
}
