package com.uade.huellitas.domain.usecase.location

import com.uade.huellitas.domain.model.DeviceLocationResult
import com.uade.huellitas.domain.model.Location
import com.uade.huellitas.domain.model.ReferenceLocationSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ResolveReferenceLocationUseCaseTest {

    private lateinit var getCurrentDeviceLocationUseCase: GetCurrentDeviceLocationUseCase
    private lateinit var geocodeAddressUseCase: GeocodeAddressUseCase
    private lateinit var resolveReferenceLocationUseCase: ResolveReferenceLocationUseCase

    @Before
    fun setUp() {
        getCurrentDeviceLocationUseCase = mockk()
        geocodeAddressUseCase = mockk()
        resolveReferenceLocationUseCase = ResolveReferenceLocationUseCase(
            getCurrentDeviceLocationUseCase,
            geocodeAddressUseCase
        )
    }

    @Test
    fun `uses current device location when available`() = runTest {
        val currentLocation = Location(-34.6037, -58.3816)
        coEvery { getCurrentDeviceLocationUseCase() } returns DeviceLocationResult.Available(currentLocation)

        val result = resolveReferenceLocationUseCase("Palermo, CABA")

        assertEquals(ReferenceLocationSource.CURRENT_DEVICE, result.source)
        assertEquals(currentLocation, result.location)
        assertEquals(ResolveReferenceLocationUseCase.CURRENT_DEVICE_LABEL, result.label)
    }

    @Test
    fun `falls back to user profile location when permission is denied`() = runTest {
        val profileLocation = Location(-34.5880, -58.4300, "Palermo")
        coEvery { getCurrentDeviceLocationUseCase() } returns DeviceLocationResult.PermissionDenied
        coEvery { geocodeAddressUseCase("Palermo, CABA") } returns profileLocation

        val result = resolveReferenceLocationUseCase("Palermo, CABA")

        assertEquals(ReferenceLocationSource.USER_PROFILE, result.source)
        assertEquals(profileLocation, result.location)
        assertEquals("Palermo, CABA", result.label)
    }

    @Test
    fun `uses default center when there is no device or profile location`() = runTest {
        coEvery { getCurrentDeviceLocationUseCase() } returns DeviceLocationResult.Unavailable
        coEvery { geocodeAddressUseCase(any()) } returns null

        val result = resolveReferenceLocationUseCase("Ubicacion desconocida")

        assertEquals(ReferenceLocationSource.DEFAULT, result.source)
        assertEquals(ResolveReferenceLocationUseCase.DEFAULT_LABEL, result.label)
        assertEquals(ResolveReferenceLocationUseCase.DEFAULT_LOCATION, result.location)
    }
}
